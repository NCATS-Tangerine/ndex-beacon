package bio.knowledge.server.impl;

import java.util.ArrayList;
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
						
	// todo: handle bad input
	// - option: don't ask ndex if blank
	// - need: return nicely if invalid 
	//   - example: statement id	
	
	// todo: paging
	// - option: only return node if has real curie?
	// - option: try to boost relevant results
	// - option: change default pagesize
	// - option: stop once found enough
	// - option: redo search with curie(s) if found
	// - option: ignore null named?
	//   - example: no label for cases like 55c84fa4-01b4-11e5-ac0f-000c29cb28fb AKT (debug 1) (function weirdness)
	
	// todo: improve exactmatches
	//  - option: take advantage of HGNC, bp:id, bp:db (etc fields) somehow
	
	// todo: get more details
	// - option: use indra
		
	// todo: use non- colon for ndexids...
	
	// todo: block non-curie, non-ndex conceptIds
	
	// todo: fix: remove property duplicates
	
	// todo: deserialize aspectlist properly
				
	@Autowired
	private SearchBuilder searchBuilder;
	
	@Autowired
	private NdexClient ndex;
	
	@Autowired
	private Translator translator;
	
	private static final int PAGE_SIZE = 5;
	private static final long TIMEOUT = 10;
	private static final TimeUnit TIMEUNIT = TimeUnit.SECONDS;
		
	
	private static Integer fix(Integer integer) {
		return integer == null? 1 : integer;
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

	
	private boolean isNdexId(String conceptId) {
		
		try {
			if (conceptId.length() >= 38) {
				UUID.fromString(conceptId.substring(0, 36));
				return conceptId.charAt(36) == ':';
			}
		
		} catch (IllegalArgumentException e) {}
		
		return false;
	}
		
	private List<Graph> search(Function<String, BasicQuery> makeJson, String luceneSearch, int pageNumber, int pageSize) {
		
		SearchString networkSearch = searchBuilder.networksBy(luceneSearch);
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
		List<Graph> graphs = new ArrayList<>();
		
		for (String conceptId : c) {
			if (isNdexId(conceptId)) {
			
				String[] half = conceptId.split(":", 2);
				String networkId = half[0];
				String nodeId = half[1];
			
				String luceneSearch = searchBuilder.id(nodeId);
				BasicQuery subnetQuery = makeJson.apply(luceneSearch);
				
				try {
					Network network = ndex.queryNetwork(networkId, subnetQuery).get(TIMEOUT, TIMEUNIT);
					Graph graph = new Graph(network);
					graphs.add(graph);
					
				} catch (InterruptedException | ExecutionException | TimeoutException e) {
				}
				
			} else {
				realCuries.add(conceptId);
			}
		}
		
		if (!realCuries.isEmpty()) {
		
			List<String> phrases = Util.map(searchBuilder::phrase, realCuries);
			String luceneSearch = searchBuilder.or(phrases);
			
			List<Graph> results = search(makeJson, luceneSearch, 0, PAGE_SIZE);
			graphs.addAll(results);
		
		}
		
		return graphs;
	}
	
	
	private void combineDuplicates(Collection<Node> nodes) {
		
		Predicate<Node> hasCurie = n -> n.getRepresents() != null || n.has("alias");
		List<Node> nodesWithCuries = Util.filter(hasCurie, nodes);
		
		Map<String, Node> aliasing = new HashMap<>();
		
		for (Node node : nodesWithCuries) {
			
			List<String> aliases = node.get("alias");
			if (node.getRepresents() != null)
				aliases.add(node.getRepresents());
			
			for (String alias : aliases) {
				if (aliasing.containsKey(alias)) {
					
					Node canonical = aliasing.get(alias);
//					if (node.has("type") && !canonical.has("type"))
//						canonical.addAttribute(node.getAttribute("type"));
					
					Consumer<Attribute> attachAttribute = a -> canonical.addAttribute(a);
					node.getAttributes().forEach(attachAttribute);
					
					if (!node.getName().equals(canonical.getName()))
						canonical.addSynonym(node.getName());
					
					nodes.remove(node);
					
				} else {
					aliasing.put(alias, node);
				}
			}
		}
		
	}
	
	public ResponseEntity<List<InlineResponse2002>> getConcepts(
			String keywords, String semgroups, Integer pageNumber, Integer pageSize) {
		
		keywords = fix(keywords);
		semgroups = fix(semgroups);
		pageNumber = fix(pageNumber) - 1;
		pageSize = fix(pageSize);
		
		String luceneSearch = searchBuilder.startsWith(keywords);
		List<Graph> graphs = search(searchBuilder::nodesBy, luceneSearch, pageNumber, pageSize);		
		Collection<Node> nodes = Util.flatmap(Graph::getNodes, graphs);
		combineDuplicates(nodes);
		List<InlineResponse2002> concepts = Util.map(translator::nodeToConcept, nodes);
		
		return ResponseEntity.ok(concepts);
	}
	
	public ResponseEntity<List<InlineResponse2001>> getConceptDetails(String conceptId) {
		
		conceptId = fix(conceptId);
			
		List<Graph> graphs = searchByIds(searchBuilder::nodesBy, Util.list(conceptId));		
		Collection<Node> nodes = Util.flatmap(Graph::getNodes, graphs);
		combineDuplicates(nodes);
		List<InlineResponse2001>  conceptDetails = Util.map(translator::nodeToConceptDetails, nodes);
		
		return ResponseEntity.ok(conceptDetails);
	}

	
	public ResponseEntity<List<InlineResponse2003>> getStatements(
			List<String> c, Integer pageNumber, Integer pageSize, String keywords, String semgroups) {
		
		c = fix(c);
		pageNumber = fix(pageNumber) - 1;
		pageSize = fix(pageSize);
		keywords = fix(keywords); // todo: handle
		semgroups = fix(semgroups);
		
		List<Graph> graphs = searchByIds(searchBuilder::edgesBy, c);		
		Collection<Edge> edges = Util.flatmap(Graph::getEdges, graphs);
		List<InlineResponse2003> statements = Util.map(translator::edgeToStatement, edges);
		
		return ResponseEntity.ok(statements);
	}

	public ResponseEntity<List<InlineResponse2004>> getEvidence(
			String statementId, String keywords, Integer pageNumber, Integer pageSize) {

		statementId = fix(statementId);
		keywords = fix(keywords);
		pageNumber = fix(pageNumber) - 1;
		pageSize = fix(pageSize);
		
		int split = statementId.lastIndexOf(":");
		String conceptId = statementId.substring(0, split);
		Long statement = Long.valueOf(statementId.substring(split + 1));
		
		List<Graph> graphs = searchByIds(searchBuilder::edgesBy, Util.list(conceptId));		
		Collection<Edge> relatedEdges = Util.flatmap(Graph::getEdges, graphs);
		
		Predicate<Edge> wasRequested = e -> e.getId().equals(statement);
		Edge edge = Util.filter(wasRequested, relatedEdges).get(0);
		List<Citation> citations = edge.getCitations(); // todo: also put citations as evidence (eg id but no text?)
		
		List<InlineResponse2004> evidence = citations == null? new ArrayList<>() : Util.map(translator::citationToEvidence, citations);
		
		return ResponseEntity.ok(evidence);
	}
	
// todo: call from getstatements
	private Set<String> getAliases(List<String> c) {
		
		List<Graph> graphs = searchByIds(searchBuilder::nodesBy, c);		
		Collection<Node> nodes = Util.flatmap(Graph::getNodes, graphs);
		
		Function<Node, List<String>> getAliases = Util.curryRight(Node::get, "alias");
		List<String> aliases = Util.flatmap(getAliases, nodes);
		
		Set<String> set = new HashSet<>();
		set.addAll(aliases);
		
		return set;
	}

	public ResponseEntity<List<String>> getExactMatchesToConcept(String conceptId) {
		
		conceptId = fix(conceptId);
		
		Set<String> set = getAliases(Util.list(conceptId));
		set.add(conceptId);
		
		List<String> exactMatches = Util.list(set);
		return ResponseEntity.ok(exactMatches);
	}
	
	public ResponseEntity<List<String>> getExactMatchesToConceptList(List<String> c) {

		c = fix(c);
		
		Set<String> set = getAliases(c);
		set.removeAll(c);
		
		List<String> exactMatches = Util.list(set);
		return ResponseEntity.ok(exactMatches);
	}
	
	
	public ResponseEntity<List<InlineResponse200>> linkedTypes() {
		return ResponseEntity.ok(new ArrayList<>());
	}

}
