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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import bio.knowledge.server.impl.Cache.CacheLocation;
import bio.knowledge.server.json.Attribute;
import bio.knowledge.server.json.BasicQuery;
import bio.knowledge.server.json.Citation;
import bio.knowledge.server.json.Edge;
import bio.knowledge.server.json.Network;
import bio.knowledge.server.json.NetworkId;
import bio.knowledge.server.json.NetworkList;
import bio.knowledge.server.json.Node;
import bio.knowledge.server.json.SearchString;
import bio.knowledge.server.model.Annotation;
import bio.knowledge.server.model.Concept;
import bio.knowledge.server.model.ConceptWithDetails;
import bio.knowledge.server.model.Predicate;
import bio.knowledge.server.model.Statement;
import bio.knowledge.server.model.Summary;

@Service
public class ControllerImpl {

	private static Logger _logger = LoggerFactory.getLogger(ControllerImpl.class);	

	@Autowired
	private Cache cache;

	@Autowired
	private SearchBuilder search;
	
	@Autowired
	private NdexClient ndex;
	
	@Autowired
	private Translator translator;

	@Autowired 
	PredicatesRegistry predicateRegistry;

	private static final int DEFAULT_PAGE_SIZE = 3;
	private static final long TIMEOUT = 8;
	private static final TimeUnit TIMEUNIT = TimeUnit.SECONDS;
		
	
	private void log(Exception e) {
		_logger.error(e.getClass() + ": " + e.getMessage());
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
				return conceptId.charAt(36) == Translator.NETWORK_NODE_DELIMITER_CHAR;
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
		//futures = Util.map(this::get, futures);
		
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
	
	private List<Graph> searchByIds(Function<String, BasicQuery> makeJson, List<String> c, int pageNumber, int pageSize) {
		
		List<String> realCuries = new ArrayList<>();
		List<CompletableFuture<Network>> futures = new ArrayList<>();
		List<Graph> graphs = new ArrayList<>();
		
		for (String conceptId : c) {
			if (isNdexId(conceptId)) {
				
				// NOT SURE WHY THIS IS NEEDED...
				//if (pageNumber > 0) continue;
			
				String[] half = conceptId.split(Translator.NETWORK_NODE_DELIMITER, 2);
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
			
			List<Graph> results = search(makeJson, luceneSearch, pageNumber, pageSize);
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
		
		List<Graph> graphs = searchByIds(search::nodesBy, c, 0, DEFAULT_PAGE_SIZE);		
		Collection<Node> nodes = Util.flatmap(Graph::getNodes, graphs);
		
		Function<Node, List<String>> getAliases = Util.curryRight(Node::get, "alias");
		List<String> aliases = Util.flatmap(getAliases, nodes);
		List<String> curies = Util.filter(Node::isCurie, aliases);
		
		Set<String> set = new HashSet<>();
		set.addAll(curies);
		
		return set;
	}
	
	
	private void combineDuplicates(Collection<Node> nodes) {
				
		java.util.function.Predicate<Node> hasCurie = n -> n.getRepresents() != null || n.has("alias");
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
					
					String nodeName = node.getName();
					
					if (! Util.nullOrEmpty(nodeName) )
						if( nodeName.equals(canonical.getName()))
							canonical.addSynonym(node.getName());
					
					node.getEdges().forEach(e -> canonical.addEdge(e));
					
					nodes.remove(node);
					
				} else {
					aliasing.put(alias, node);
				}
			}
		}
		
	}
	
	private Collection<Node> filterSemanticGroup(Collection<Node> nodes, String semanticGroups) {
		
		if (semanticGroups.isEmpty()) return nodes;
		
		List<String> types = Arrays.asList(semanticGroups.split(" "));		
		java.util.function.Predicate<Node> hasType = n -> types.contains(translator.makeSemGroup(n));
		nodes = Util.filter(hasType, nodes);
		return nodes;
	}
	
	private Collection<Edge> filterByText(Collection<Edge> edges, String keywords) {
		
		if (keywords.isEmpty()) return edges;

		List<String> words = Arrays.asList(keywords.split(" "));
		
		java.util.function.Predicate<Edge> matches = e -> containsAll(e.getName() + " " + e.getSubject().getName() + " " + e.getObject().getName(), words);
			
		Collection<Edge> matching = Util.filter(matches, edges);
		return matching;
	}
	
	private Collection<Edge> filterByPredicate(Collection<Edge> edges, String predicateFilter) {
		
		if (predicateFilter.isEmpty()) return edges;

		// these are Predicate Ids from the client
		List<String> predicateIds = Arrays.asList(predicateFilter.split(" "));
		
		// Translate predicate ids to their names
		List<String> relations = new ArrayList<String>();
		for(String pid : predicateIds) 
			if(predicateRegistry.containsKey(pid)) 
				relations.add(predicateRegistry.get(pid).getName());
			// else - ignore as unknown?
		
		java.util.function.Predicate<Edge> matches = e -> containsAll( e.getName(), relations );
		Collection<Edge> matching = Util.filter(matches, edges);
		
		return matching;
	}
	
	private List<Citation> filterMatching(List<Citation> citations, String keywords) {
		
		if (keywords.isEmpty()) return citations;

		List<String> words = Arrays.asList(keywords.split(" "));
		java.util.function.Predicate<Citation> matches = c -> containsAll(c.getFullText(), words);
			
		List<Citation> matching = Util.filter(matches, citations);
		return matching;
	}

	public List<? extends CachedEntity> getPage(List<? extends CachedEntity> items, Integer pageNumber, Integer pageSize) {
		Integer size = items.size();
		if(size==0) {
			return new ArrayList<>();
		}
		if(pageNumber<0) pageNumber = 0;
		if(pageSize<1)   pageSize   = size;  // default to full size of list
		Integer fromIndex = pageNumber*pageSize;
		if(fromIndex>size) {
			return new ArrayList<>();
		}
		Integer toIndex = fromIndex+pageSize;
		toIndex = toIndex>size?size:toIndex; // coerce upper bound
		
		return items.subList(fromIndex, toIndex);
	}
	
	public ResponseEntity<List<Concept>> getConcepts(String keywords, String semanticGroups, Integer pageNumber, Integer pageSize) {
		try {
			
			keywords = fix(keywords);
			semanticGroups = fix(semanticGroups);
			pageNumber = fix(pageNumber) - 1;
			
			//pageSize = DEFAULT_PAGE_SIZE; //fix(pageSize);
			pageSize = fix(pageSize);
			
			List<Concept> concepts = null ;
			
			// I attempt caching of the whole retrieved set
			CacheLocation cacheLocation = 
					cache.searchForResultSet(
							"Concept", 
							keywords, 
							new String[] { keywords, semanticGroups }
					);

			@SuppressWarnings("unchecked")
			List<Concept> cachedResult = 
					(List<Concept>)cacheLocation.getResultSet();
			
			if(cachedResult==null) {
				
				String luceneSearch = search.startsWith(keywords);
				List<Graph> graphs = search(search::nodesBy, luceneSearch, pageNumber, pageSize);		
				
				Collection<Node> nodes = Util.flatmap(Graph::getNodes, graphs);
				combineDuplicates(nodes);
				Collection<Node> ofType = filterSemanticGroup(nodes, semanticGroups);
				
				concepts = Util.map(translator::nodeToConcept, ofType);
			
				cacheLocation.setResultSet(concepts);
				
			} else {
				concepts = cachedResult;
			}
			
			@SuppressWarnings("unchecked")
			// Paging workaround since nDex paging doesn't seem to work as published?
			List<Concept> page = (List<Concept>)getPage(concepts, pageNumber, pageSize);
			
			return ResponseEntity.ok(page);
		
		} catch (Exception e) {
			log(e);
			return ResponseEntity.ok(new ArrayList<Concept>());
		}
	}
	
	public ResponseEntity<List<ConceptWithDetails>> getConceptDetails(String conceptId) {
		
		try {
			
			conceptId = fix(conceptId);
				
			List<Graph> graphs = searchByIds(search::nodesBy, Util.list(conceptId), 1, 100);		
			Collection<Node> nodes = Util.flatmap(Graph::getNodes, graphs);
			combineDuplicates(nodes);
			
			List<ConceptWithDetails>  conceptDetails = Util.map(translator::nodeToConceptDetails, nodes);
			
			return ResponseEntity.ok(conceptDetails);
		
		} catch (Exception e) {
			log(e);
			return ResponseEntity.ok(new ArrayList<>());
		}
	}


	public ResponseEntity<List<Predicate>> getPredicates() {
		List<Predicate> responses = new ArrayList<Predicate>(predicateRegistry.values());
		return ResponseEntity.ok(responses);		
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
	
	
	public ResponseEntity<List<Statement>> getStatements(
			List<String> c, 
			String keywords, 
			String semanticGroups,
			String relations,
			Integer pageNumber, 
			Integer pageSize 
	) {
		try {
			
			c = fix(c);
			pageNumber = fix(pageNumber) - 1;
			
			//pageSize = DEFAULT_PAGE_SIZE; //fix(pageSize);
			pageSize = fix(pageSize);
			
			keywords = fix(keywords);
			semanticGroups = fix(semanticGroups);
			relations = fix(relations);
			
			List<Statement> statements = null ;
			
			// I attempt caching of the whole retrieved set
			CacheLocation cacheLocation = 
					cache.searchForResultSet(
							"Statement", 
							c.toString(), 
							new String[] { c.toString(), keywords, semanticGroups, relations }
					);

			@SuppressWarnings("unchecked")
			List<Statement> cachedResult = 
					(List<Statement>)cacheLocation.getResultSet();
			
			if(cachedResult==null) {			
			
				Set<String> aliases = getAliases(c);
				aliases.addAll(c);
				
				List<Graph> graphs = searchByIds(search::edgesBy, Util.list(aliases), pageNumber, pageSize);
				
				Collection<Node> nodes = Util.flatmap(Graph::getNodes, graphs);
				Collection<Node> ofType = filterSemanticGroup(nodes, semanticGroups);
				
				Collection<Edge> edges = Util.flatmap(Node::getEdges, ofType);
				
				Collection<Edge> edgesByRelations = filterByPredicate(edges, relations);
						 
				Collection<Edge> matching = filterByText(edgesByRelations, keywords);
				
				statements = Util.map(translator::edgeToStatement, matching);
				
				//cache.getResultSetCache().put(cacheKey, searchedConceptResult);
				cacheLocation.setResultSet(statements);
				
			} else {
				statements = cachedResult;
			}
			
			@SuppressWarnings("unchecked")
			// Paging workaround since nDex paging doesn't seem to work as published?
			List<Statement> page = (List<Statement>)getPage(statements, pageNumber, pageSize);
			
			return ResponseEntity.ok(page);
		
		} catch (Exception e) {
			log(e);
			return ResponseEntity.ok(new ArrayList<>());
		}
	}

	public ResponseEntity<List<Annotation>> getEvidence(String statementId, String keywords, Integer pageNumber, Integer pageSize) {
		try {
		
			statementId = fix(statementId);
			keywords = fix(keywords);
			pageNumber = fix(pageNumber) - 1;
			
			//pageSize = DEFAULT_PAGE_SIZE; //fix(pageSize);
			pageSize = fix(pageSize);
			
			if(statementId.startsWith(Translator.NDEX_NS)) {
				statementId = statementId.replaceAll("^"+Translator.NDEX_NS, "");
			}
			
			String[] half = statementId.split("_", 2);
			String conceptId = half[0];
			Long statement = Long.valueOf(half[1]);

			List<Graph> graphs = searchByIds(search::edgesBy, Util.list(conceptId), pageNumber, pageSize);
			
			Collection<Edge> relatedEdges = Util.flatmap(Graph::getEdges, graphs);
			
			java.util.function.Predicate<Edge> wasRequested = e -> e.getId().equals(statement);
			List<Edge> maybeEdge = Util.filter(wasRequested, relatedEdges);
			
			if (maybeEdge.size() == 1) {
				
				List<Annotation> evidence = new ArrayList<Annotation>();

				/*
				 *  Insert the current Graph network identifier 
				 *  as one piece of "evidence" alongside 
				 *  any other discovered citation evidence
				 */
				Annotation networkEvidence = new Annotation();
				String[] idPart = statementId.split(Translator.NETWORK_NODE_DELIMITER, 2);
				networkEvidence.setId("ndex.network:"+idPart[0]);
				networkEvidence.setLabel("nDex Network");
				networkEvidence.setType("TAS");
				evidence.add(networkEvidence);
				
				// Add any edge Citation annotation, if available
				Edge edge = maybeEdge.get(0);
				List<Citation> citations = edge.getCitations();
				List<Citation> matching = filterMatching(citations, keywords);
				
				if(citations != null)
					evidence.addAll( Util.map(translator::citationToEvidence, matching) );
				
				return ResponseEntity.ok(evidence);
			
			} else {
				return ResponseEntity.ok(new ArrayList<>());
			}
			
		} catch (Exception e) {
			log(e);
			return ResponseEntity.ok(new ArrayList<>());
		}
	}


	public ResponseEntity<List<Summary>> linkedTypes() {
		
		List<Summary> types = new ArrayList<Summary>();
		
		// Hard code some known types... See Translator.makeSemGroup()
		Summary GENE_Type = new Summary();
		GENE_Type.setId("GENE");
		types.add(GENE_Type);
		
		Summary CHEM_Type = new Summary();
		CHEM_Type.setId("CHEM");
		types.add(CHEM_Type);
		
		Summary DISO_Type = new Summary();
		DISO_Type.setId("DISO");
		types.add(DISO_Type);
		
		Summary PHYS_Type = new Summary();
		PHYS_Type.setId("PHYS");
		types.add(PHYS_Type);
		
		Summary OBJC_Type = new Summary();
		OBJC_Type.setId("OBJC");
		types.add(OBJC_Type);
		
		return ResponseEntity.ok(types);
	}
}
