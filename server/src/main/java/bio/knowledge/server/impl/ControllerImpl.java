package bio.knowledge.server.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import bio.knowledge.server.json.Attribute;
import bio.knowledge.server.json.BasicQuery;
import bio.knowledge.server.json.Citation;
import bio.knowledge.server.json.Edge;
import bio.knowledge.server.json.Network;
import bio.knowledge.server.json.NetworkId;
import bio.knowledge.server.json.NetworkList;
import bio.knowledge.server.json.Node;
import bio.knowledge.server.json.SearchString;
import bio.knowledge.server.model.InlineResponse200;
import bio.knowledge.server.model.InlineResponse2001;
import bio.knowledge.server.model.InlineResponse2002;
import bio.knowledge.server.model.InlineResponse2003;
import bio.knowledge.server.model.InlineResponse2004;

@Service
public class ControllerImpl {
		
	@Autowired
	private SearchBuilder search;
	
	@Autowired
	private NdexClient ndex;
	
	@Autowired
	private Translator translator;
	
	private static final int DEFAULT_PAGE_SIZE = 5;
	private static final long TIMEOUT = 8;
	private static final TimeUnit TIMEUNIT = TimeUnit.SECONDS;
		
	
	private void log(Exception e) {
		System.err.println(e.getClass() + ": " + e.getMessage());
	}
	
	
	private static Integer fix(Integer integer) {
		return integer == null || integer < 1 ? 1 : integer;
	}
	
	private static String fix(String string) {
		return string == null? "" : string;
	}
	
	private static List<String> fix(List<String> strings) {
		return Util.map(ControllerImpl::fix, strings);
	}
	
	
	private CompletableFuture<Network> get(CompletableFuture<Network> future) {
		
		return CompletableFuture.supplyAsync(() -> {
			
			try {
				return future.get(TIMEOUT, TIMEUNIT);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				return new Network();
			}
		
		});
	}

	private boolean containsAll(String string, List<String> keywords) {
		return Util.allMatch(string::contains, keywords);
	}
	
	private boolean isNdexId(String conceptId) {
		
		try {
			if (conceptId.length() >= 38) {
				UUID.fromString(conceptId.substring(0, 36));
				return conceptId.charAt(36) == ':';
			}
		
		} catch (IllegalArgumentException e) {}
		
		return false;
	}

	
	private String restrictQuery(String query) {
		return search.and(query, search.edgeCount(1, 100000));
	}
	
	private List<Graph> search(Function<String, BasicQuery> makeJson, String luceneSearch, int pageNumber, int pageSize) {
		
		SearchString networkSearch = search.networksBy(restrictQuery(luceneSearch));
		BasicQuery subnetQuery = makeJson.apply(luceneSearch);
		
		NetworkList networks = ndex.searchNetworks(networkSearch, pageNumber, pageSize);
		List<String> networkIds = Util.map(NetworkId::getExternalId, networks.getNetworks());
		
		Function<String, CompletableFuture<Network>> executeSearch = Util.curryRight(ndex::queryNetwork, subnetQuery);
		
		List<CompletableFuture<Network>> futures = Util.map(executeSearch, networkIds);
		futures = Util.map(this::get, futures);
		
		List<Network> subnetworks = new ArrayList<>();
		
		for (CompletableFuture<Network> future : futures) {
			try {
				subnetworks.add(future.get(TIMEOUT, TIMEUNIT));
			} catch (InterruptedException | ExecutionException | TimeoutException e) {	
			}
		}
		
		List<Graph> graphs = Util.map(Graph::new, subnetworks);
		return graphs;
	}
	
	private List<Graph> searchByIds(Function<String, BasicQuery> makeJson, List<String> c) {
		
		List<String> realCuries = new ArrayList<>();
		List<CompletableFuture<Network>> futures = new ArrayList<>();
		List<Graph> graphs = new ArrayList<>();
		
		for (String conceptId : c) {
			if (isNdexId(conceptId)) {
			
				String[] half = conceptId.split(":", 2);
				String networkId = half[0];
				String nodeId = half[1];
			
				String luceneSearch = search.id(nodeId);
				BasicQuery subnetQuery = makeJson.apply(luceneSearch);
				
				CompletableFuture<Network> network = get(ndex.queryNetwork(networkId, subnetQuery));
				futures.add(network);
			
			} else if (Node.isCurie(conceptId)){
				realCuries.add(conceptId);
			}
		}
		
		if (!realCuries.isEmpty()) {
		
			List<String> phrases = Util.map(search::phrase, realCuries);
			String luceneSearch = search.or(phrases);
			
			List<Graph> results = search(makeJson, luceneSearch, 0, DEFAULT_PAGE_SIZE);
			graphs.addAll(results);
		
		}
		
		for (CompletableFuture<Network> future : futures) {
			try {
				Graph graph = new Graph(future.get(TIMEOUT, TIMEUNIT));
				graphs.add(graph);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
			}
		}
		
		return graphs;
	}
	
	private Set<String> getAliases(List<String> c) {
		
		List<Graph> graphs = searchByIds(search::nodesBy, c);		
		Collection<Node> nodes = Util.flatmap(Graph::getNodes, graphs);
		
		Function<Node, List<String>> getAliases = Util.curryRight(Node::get, "alias");
		List<String> aliases = Util.flatmap(getAliases, nodes);
		List<String> curies = Util.filter(Node::isCurie, aliases);
		
		Set<String> set = new HashSet<>();
		set.addAll(curies);
		
		return set;
	}
	
	
	private void combineDuplicates(Collection<Node> nodes) {
				
		Predicate<Node> hasCurie = n -> n.getRepresents() != null || n.has("alias");
		List<Node> nodesWithCuries = Util.filter(hasCurie, nodes);
		
		Map<String, Node> aliasing = new HashMap<>();
		
		for (Node node : nodesWithCuries) {
			
			Set<String> aliases = new HashSet<>(node.get("alias"));
			if (node.getRepresents() != null)
				aliases.add(node.getRepresents());
			
			for (String alias : aliases) {
				if (aliasing.containsKey(alias)) {
					
					Node canonical = aliasing.get(alias);
					
					Consumer<Attribute> attachAttribute = a -> canonical.addAttribute(a);
					node.getAttributes().forEach(attachAttribute);
					
					if (!node.getName().equals(canonical.getName()))
						canonical.addSynonym(node.getName());
					
					node.getEdges().forEach(e -> canonical.addEdge(e));
					
					nodes.remove(node);
					
				} else {
					aliasing.put(alias, node);
				}
			}
		}
		
	}
	
	private Collection<Node> filterTypes(Collection<Node> nodes, String semgroups) {
		
		if (semgroups.isEmpty()) return nodes;
		
		List<String> types = Arrays.asList(semgroups.split(" "));		
		Predicate<Node> hasType = n -> types.contains(translator.makeSemGroup(n));
		nodes = Util.filter(hasType, nodes);
		return nodes;
	}
	
	private Collection<Edge> filterMatching(Collection<Edge> edges, String keywords) {
		
		if (keywords.isEmpty()) return edges;

		List<String> words = Arrays.asList(keywords.split(" "));
		
		Predicate<Edge> matches = e -> containsAll(e.getName() + " " + e.getSubject().getName() + " " + e.getObject().getName(), words);
			
		Collection<Edge> matching = Util.filter(matches, edges);
		return matching;
	}
	
	private List<Citation> filterMatching(List<Citation> citations, String keywords) {
		
		if (keywords.isEmpty()) return citations;

		List<String> words = Arrays.asList(keywords.split(" "));
		Predicate<Citation> matches = c -> containsAll(c.getFullText(), words);
			
		List<Citation> matching = Util.filter(matches, citations);
		return matching;
	}
	
	
	public ResponseEntity<List<InlineResponse2002>> getConcepts(String keywords, String semgroups, Integer pageNumber, Integer pageSize) {
		try {
			
			keywords = fix(keywords);
			semgroups = fix(semgroups);
			pageNumber = fix(pageNumber) - 1;
			pageSize = fix(pageSize);
			
			String luceneSearch = search.startsWith(keywords);
			List<Graph> graphs = search(search::nodesBy, luceneSearch, pageNumber, pageSize);		
			
			Collection<Node> nodes = Util.flatmap(Graph::getNodes, graphs);
			combineDuplicates(nodes);
			Collection<Node> ofType = filterTypes(nodes, semgroups);
			
			List<InlineResponse2002> concepts = Util.map(translator::nodeToConcept, ofType);
			return ResponseEntity.ok(concepts);
		
		} catch (Exception e) {
			log(e);
			return ResponseEntity.ok(new ArrayList<>());
		}
	}
	
	public ResponseEntity<List<InlineResponse2001>> getConceptDetails(String conceptId) {
		try {
			
			conceptId = fix(conceptId);
				
			List<Graph> graphs = searchByIds(search::nodesBy, Util.list(conceptId));		
			Collection<Node> nodes = Util.flatmap(Graph::getNodes, graphs);
			combineDuplicates(nodes);
			List<InlineResponse2001>  conceptDetails = Util.map(translator::nodeToConceptDetails, nodes);
			
			return ResponseEntity.ok(conceptDetails);
		
		} catch (Exception e) {
			log(e);
			return ResponseEntity.ok(new ArrayList<>());
		}
	}

	
	public ResponseEntity<List<InlineResponse2003>> getStatements(List<String> c, Integer pageNumber, Integer pageSize, String keywords, String semgroups) {
		try {
			
			c = fix(c);
			pageNumber = fix(pageNumber) - 1;
			pageSize = fix(pageSize);
			keywords = fix(keywords);
			semgroups = fix(semgroups);
			
			Set<String> aliases = getAliases(c);
			aliases.addAll(c);
			List<Graph> graphs = searchByIds(search::edgesBy, Util.list(aliases));
			
//			List<Graph> graphs = searchByIds(search::edgesBy, c);

			Collection<Node> nodes = Util.flatmap(Graph::getNodes, graphs);
			Collection<Node> ofType = filterTypes(nodes, semgroups);
			
			Collection<Edge> edges = Util.flatmap(Node::getEdges, ofType);
			Collection<Edge> matching = filterMatching(edges, keywords);
			
			List<InlineResponse2003> statements = Util.map(translator::edgeToStatement, matching);
			return ResponseEntity.ok(statements);
		
		} catch (Exception e) {
			log(e);
			return ResponseEntity.ok(new ArrayList<>());
		}
	}

	public ResponseEntity<List<InlineResponse2004>> getEvidence(String statementId, String keywords, Integer pageNumber, Integer pageSize) {
		try {
		
			statementId = fix(statementId);
			keywords = fix(keywords);
			pageNumber = fix(pageNumber) - 1;
			pageSize = fix(pageSize);
			
			String[] half = statementId.split("_", 2);
			String conceptId = half[0];
			Long statement = Long.valueOf(half[1]);
			
			List<Graph> graphs = searchByIds(search::edgesBy, Util.list(conceptId));		
			Collection<Edge> relatedEdges = Util.flatmap(Graph::getEdges, graphs);
			
			Predicate<Edge> wasRequested = e -> e.getId().equals(statement);
			Edge edge = Util.filter(wasRequested, relatedEdges).get(0);
			
			List<Citation> citations = edge.getCitations();
			List<Citation> matching = filterMatching(citations, keywords);
			
			List<InlineResponse2004> evidence = citations == null? new ArrayList<>() : Util.map(translator::citationToEvidence, matching);
			
			return ResponseEntity.ok(evidence);
			
		} catch (Exception e) {
			log(e);
			return ResponseEntity.ok(new ArrayList<>());
		}
	}

	
	public ResponseEntity<List<String>> getExactMatchesToConcept(String conceptId) {
		try {
			
			conceptId = fix(conceptId);
			
			Set<String> set = getAliases(Util.list(conceptId));
			if (Node.isCurie(conceptId))
				set.add(conceptId);
			
			List<String> exactMatches = Util.list(set);
			return ResponseEntity.ok(exactMatches);
		
		} catch (Exception e) {
			log(e);
			return ResponseEntity.ok(new ArrayList<>());
		}
	}
	
	public ResponseEntity<List<String>> getExactMatchesToConceptList(List<String> c) {
		try {
			
			c = fix(c);
			
			Set<String> set = getAliases(c);
			set.removeAll(c);
			
			List<String> exactMatches = Util.list(set);
			return ResponseEntity.ok(exactMatches);
			
		} catch (Exception e) {
			log(e);
			return ResponseEntity.ok(new ArrayList<>());
		}
	}
	
	
	public ResponseEntity<List<InlineResponse200>> linkedTypes() {
		return ResponseEntity.ok(new ArrayList<>());
	}

}
