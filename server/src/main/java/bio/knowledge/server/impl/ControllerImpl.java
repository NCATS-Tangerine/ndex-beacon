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
import bio.knowledge.server.model.BeaconAnnotation;
import bio.knowledge.server.model.BeaconConcept;
import bio.knowledge.server.model.BeaconConceptCategory;
import bio.knowledge.server.model.BeaconConceptWithDetails;
import bio.knowledge.server.model.BeaconKnowledgeMapObject;
import bio.knowledge.server.model.BeaconKnowledgeMapPredicate;
import bio.knowledge.server.model.BeaconKnowledgeMapStatement;
import bio.knowledge.server.model.BeaconKnowledgeMapSubject;
import bio.knowledge.server.model.BeaconPredicate;
import bio.knowledge.server.model.BeaconStatement;
import bio.knowledge.server.model.ExactMatchResponse;
import bio.knowledge.server.ontology.OntologyService;

@Service
public class ControllerImpl {

	private static Logger _logger = LoggerFactory.getLogger(ControllerImpl.class);
	
	@Autowired OntologyService ontology;
	@Autowired private Cache cache;
	@Autowired private SearchBuilder search;
	@Autowired private NdexClient ndex;
	@Autowired private Translator translator;
	@Autowired AliasNamesRegistry aliasRegistry;
	@Autowired PredicatesRegistry predicateRegistry;

	private static final int DEFAULT_PAGE_SIZE = 10;
	private static final long TIMEOUT = 30;
	private static final TimeUnit TIMEUNIT = TimeUnit.SECONDS;
		
	
	private void log(Exception e) {
		_logger.error(e.getClass() + ": " + e.getMessage());
		e.printStackTrace();
	}
	
	private static Integer fixPageSize(Integer integer) {
		return integer == null || integer < 1 ? DEFAULT_PAGE_SIZE : integer;
	}
	
	private static String fix(String string) {
		return string == null? "" : string;
	}
	
	private static List<String> fix(List<String> stringList ) {
		return  stringList == null? new ArrayList<String>() : Util.map(ControllerImpl::fix, stringList );
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
		
		// Remember the artificial prefix you've added internally in the ndex beacon!
		if(conceptId.startsWith(Translator.NDEX_NS)) {
			return true;
		}
		
		// ...old test, in case you
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
	
	private List<Graph> search(Function<String, BasicQuery> makeJson, String luceneSearch, int size) {
		
		SearchString networkSearch = search.networksBy(restrictQuery(luceneSearch));
		BasicQuery subnetQuery = makeJson.apply(luceneSearch);
		
		NetworkList networks = ndex.searchNetworks(networkSearch, size);
		
		List<String> networkIds = Util.map(NetworkId::getExternalId, networks.getNetworks());
		
		Function<String, CompletableFuture<Network>> executeSearch = Util.curryRight(ndex::queryNetwork, subnetQuery);
		
		List<CompletableFuture<Network>> futures = Util.map(executeSearch, networkIds);
		//futures = Util.map(this::get, futures);
		
		List<Network> subnetworks = new ArrayList<>();
		
		for (CompletableFuture<Network> future : futures) {
			try {
				subnetworks.add(future.get(TIMEOUT, TIMEUNIT));
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				log(e);
			}
		}
		List<Graph> graphs = Util.map(Graph::new, subnetworks);
		
		return graphs;
	}
	
	private List<Graph> searchByIds(Function<String, BasicQuery> makeJson, List<String> c, int size) {
		
		_logger.debug("Entering searchByIds(c: '"+String.join(",", c)+"')");
		
		List<String> realCuries = new ArrayList<>();
		List<CompletableFuture<Network>> futures = new ArrayList<>();
		List<Graph> graphs = new ArrayList<>();
		
		for (String conceptId : c) {
			if (isNdexId(conceptId)) {
				
				// Remember to remove the artificial prefix you've added internally in the ndex beacon!
				if(conceptId.startsWith(Translator.NDEX_NS))
					conceptId = conceptId.replace(Translator.NDEX_NS,"");

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
			
			List<Graph> results = search(makeJson, luceneSearch, size);
			
			graphs.addAll(results);
		}
		
		for (CompletableFuture<Network> future : futures) {
			try {
				Graph graph = new Graph(future.get(TIMEOUT, TIMEUNIT));
				graphs.add(graph);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
			}
		}
		
		_logger.debug("Exiting searchByIds(): "+new Integer(graphs.size())+" graphs found?");
		
		return graphs;
	}
	
	private  Set<String> addCachedAliases(Collection<String> ids) {
		
		Set<String> aliases = new HashSet<>();
		/*
		 * Augment full set of aliases with any names 
		 * from the alias names registry?
		 */
		for(String id : ids) {
			aliases.add(id);
			if(aliasRegistry.containsKey(id))
				aliases.addAll(aliasRegistry.get(id));
		}
		return aliases;
	}
	/*
	 * This only returns the aliases of the members of the 'c' list of input identifiers
	 * but not directly the 'c' identifiers themselves nor their locally cached related ids
	 */
	private List<ExactMatchResponse> getMatchingResponses( List<String> c ) {
		
		List<ExactMatchResponse> response = new ArrayList<>();
		
		for (String curie : c) {
			ExactMatchResponse match = new ExactMatchResponse();
			
			match.setId(curie);
			
			List<Graph> graphs = searchByIds( search::nodesBy, c, DEFAULT_PAGE_SIZE );
			Collection<Node> nodes = Util.flatmap( Graph::getNodes, graphs );
			
			if (nodes.size() == 0) {
				match.setWithinDomain(false);
			} else {
				match.setWithinDomain(true);
			}
			
			Set<String> aliases = getAliases(nodes);
			
			List<String> curies  = Util.filter(Node::isCurie, aliases);
			Set<String> curiesSet = addCachedAliases(curies);
			curiesSet.removeAll(c);
			
			List<String> exactMatches = new ArrayList<String>(curiesSet);
			match.setHasExactMatches(exactMatches);
			
			response.add(match);
		}
		
		return response;
	}
	
	/**
	 * Gets only those aliases that are corroborated by multiple networks. Not all
	 * networks are reliable sources of exact match information, and this
	 * heuristic will hopefully prevent false positives.
	 */
	private Set<String> getAliases(Collection<Node> nodes) {
		Map<String, List<String>> networkAliases = new HashMap<String, List<String>>();
		
		for (Node node : nodes) {
			String networkId = node.getNetworkId();
			
			for (Attribute attribute : node.getAttributes()) {
				if (attribute.getName().equals("alias")) {
					if (networkAliases.containsKey(networkId)) {
						networkAliases.get(networkId).addAll(attribute.getValues());
						
					} else {
						List<String> aliases = new ArrayList<String>(attribute.getValues());
						networkAliases.put(networkId, aliases);
					}
				}
			}
		}
		
		Set<String> aliases = new HashSet<String>();
		
		List<String> networkIds = new ArrayList<String>(networkAliases.keySet());
		
		for (int i = 0; i < networkIds.size(); i++) {
			for (int j = i + 1; j < networkIds.size(); j++) {
				String n1 = networkIds.get(i);
				String n2 = networkIds.get(j);
				
				List<String> aliases1 = networkAliases.get(n1);
				List<String> aliases2 = networkAliases.get(n2);
				
				for (String alias : aliases1) {
					if (aliases2.contains(alias)) {
						aliases.add(alias);
					}
				}
			}
		}
		
		return aliases;
	}
	
	/*
	 * This only returns the aliases of the members of the 'c' list of input identifiers
	 * but not directly the 'c' identifiers themselves nor their locally cached related ids
	 */
	private Set<String> getAliases( List<String> c ) {
		
		List<Graph> graphs = searchByIds( search::nodesBy, c, DEFAULT_PAGE_SIZE );
		
		Collection<Node> nodes = Util.flatmap( Graph::getNodes, graphs );
		
		Set<String> aliases = getAliases(nodes);
		
		List<String> curies  = Util.filter(Node::isCurie, aliases);
		
		return addCachedAliases(curies);
	}

	
	/*
	 *  Exhaustive list of aliases including the ids
	 *  themselves and their locally cached ndex ids
	 */
	private Set<String> allAliases( List<String> ids ) {
		Set<String> aliases = getAliases( ids ); 
		aliases.addAll( addCachedAliases(ids) );
		return aliases;
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
					
					String nodeName = node.getName();
					
					if (! Util.nullOrEmpty(nodeName) )
						if( !nodeName.equals(canonical.getName()))
							canonical.addSynonym(node.getName());
					
					node.getEdges().forEach(e -> canonical.addEdge(e));
					
					nodes.remove(node);
					
				} else {
					aliasing.put(alias, node);
				}
			}
		}
		
	}
	
	/**
	 * Removes BeaconConcept from list if does not have a (Biolink) category that matches types 
	 * @param concepts
	 * @param types
	 * @return filtered list
	 */
	private List<BeaconConcept> filterSemanticGroup(List<BeaconConcept> concepts, List<String> types) {
		
		if (types.isEmpty()) return concepts;
		
		Predicate<BeaconConcept> hasType = n -> types.contains(n.getCategory());
		concepts = Util.filter(hasType, concepts);
		
		return concepts;
	}

	/*
	 * This is a variant of the semantic group filter which focuses on the semantic group of the 
	 * 'target' node side of edges, opposite of any edge node matching a source alias name?
	 */
	private boolean testTargetSemanticGroup( Edge edge, Set<String> sourceAliases, List<String> types )  {
		
		Node subject = edge.getSubject();
		String subjectId = translator.makeId( subject );

		Node object = edge.getObject();
		String objectId = translator.makeId( object );
		
		if( sourceAliases.contains(subjectId) ) 
			return types.contains(translator.inferConceptCategory(object));
			
		 else if( sourceAliases.contains(objectId) )
			return types.contains(translator.inferConceptCategory(subject));
 
		// Strange situation... one of the two id's should match a source alias!
		_logger.warn("ControllerImpl.testTargetSemanticGroup(): strange!... neither subject id '"+subjectId+
				     "' nor object id '"+objectId+"' found in source alias list '"+String.join(",", sourceAliases)+"'?");
		return false;

	}
	
	private Collection<Edge> filterSemanticGroup( Collection<Edge> edges, Set<String> sourceAliases, List<String> types) {
		
		if (types.isEmpty()) return edges;
		
		Predicate<Edge> hasTargetType = e -> testTargetSemanticGroup( e, sourceAliases, types );
		edges = Util.filter(hasTargetType, edges);
		
		return edges;
	}
	
	private Collection<Edge> filterByText( Collection<Edge> edges, List<String> keywords ) {
		
		if (keywords.isEmpty()) return edges;
		
		Predicate<Edge> matches =
				e -> containsAll(e.getName() + " " + e.getSubject().getName() + " " + e.getObject().getName(), keywords);
			
		Collection<Edge> matching = Util.filter(matches, edges);
		
		return matching;
	}
	
	private Collection<Edge> filterByPredicate(Collection<Edge> edges, List<String> predicateFilter) {
		
		if (predicateFilter.isEmpty()) return edges;

		// Translate predicate ids to their names
		List<String> relations = new ArrayList<String>();
		for(String pid : predicateFilter) 
			if(predicateRegistry.containsKey(pid)) 
				relations.add(predicateRegistry.get(pid).getEdgeLabel());
			// else - ignore as unknown?
		
		if(relations.isEmpty()) return edges;
		
		Predicate<Edge> matches = e -> containsAll( e.getName(), relations );
		Collection<Edge> matching = Util.filter(matches, edges);
		
		return matching;
	}

	private boolean testTargetId( Edge edge, Set<String> sourceAliases, Set<String> targetAliases ) {
		
		Node subject = edge.getSubject();
		String subjectId = translator.makeId( subject );

		Node object = edge.getObject();
		String objectId = translator.makeId( object );
		
		if( sourceAliases.contains(subjectId) ) 
			return targetAliases.contains(objectId);
			
		 else if( sourceAliases.contains(objectId) )
			return targetAliases.contains(subjectId);
 
		// Strange situation... one of the two id's should match a source alias!
		_logger.warn("ControllerImpl.testTargetId(): strange!... neither subject id '"+subjectId+
				     "' nor object id '"+objectId+"' found in source alias list '"+String.join(",", sourceAliases)+"'?");
		return false;
	}

	private Collection<Edge> filterByTarget( Collection<Edge> edges, Set<String> sourceAliases, Set<String> targetAliases ) {
		
		Predicate<Edge> hasTargetId = e -> testTargetId( e, sourceAliases, targetAliases );
		edges = Util.filter( hasTargetId, edges );
		
		return edges;
	}
	

	private List<Citation> filterMatching(List<Citation> citations, List<String> keywords) {
		
		if (keywords.isEmpty()) return citations;

		Predicate<Citation> matches = c -> containsAll(c.getFullText(), keywords);
			
		List<Citation> matching = Util.filter(matches, citations);
		return matching;
	}

	public List<? extends CachedEntity> getPage(List<? extends CachedEntity> items, Integer size) {
		if (items.isEmpty()) {
			return new ArrayList<>();
		}
		
		if (size > items.size()) {
			size = items.size();
		}
		
		if (size < 0) {
			size = 0;
		}
		
		return items.subList(0, size);
	}
	
	public ResponseEntity<List<BeaconConcept>> getConcepts(List<String> keywords, List<String> categories, Integer size) {
		try {
			
			keywords = fix(keywords);
			categories = fix(categories);
			size = fixPageSize(size);
			
			List<BeaconConcept> concepts = null ;
			
			String joinedKeywords = String.join(" ", keywords);
			String joinedTypes = String.join(" ", categories);
			
			// I attempt caching of the whole retrieved set
			CacheLocation cacheLocation = 
					cache.searchForResultSet(
							"Concept", 
							joinedKeywords, 
							new String[] { 
									joinedKeywords, 
									joinedTypes
							}
					);

			@SuppressWarnings("unchecked")
			List<BeaconConcept> cachedResult = 
					(List<BeaconConcept>)cacheLocation.getResultSet();
			
			if(cachedResult==null) {
				
				String luceneSearch = search.startsWith(joinedKeywords);

				List<Graph> graphs = search(search::nodesBy, luceneSearch, size);		
				
				Collection<Node> nodes = Util.flatmap(Graph::getNodes, graphs);
				combineDuplicates(nodes);
				
				/* 
				 * Execute makeId on all the returned nodes for the
				 * side effect of registering their alias ndex node ids
				 */
				Util.map(translator::makeId, nodes);
				
				concepts = Util.map(translator::nodeToConcept, nodes);
				cacheLocation.setResultSet(concepts);
				
			} else {
				concepts = cachedResult;
			}
			
			List<BeaconConcept> ofType = filterSemanticGroup(concepts, categories);
			
			@SuppressWarnings("unchecked")
			// Paging workaround since nDex paging doesn't seem to work as published?
			List<BeaconConcept> page = (List<BeaconConcept>)getPage(ofType, size);
			return ResponseEntity.ok(page);
		
		} catch (Exception e) {
			log(e);
			// TODO: remove cache if there was an error? Perhaps something different if was timeout error?
			return ResponseEntity.ok(new ArrayList<BeaconConcept>());
		}
	}
	
	public ResponseEntity<List<BeaconConceptWithDetails>> getConceptDetails(String conceptId) {

		try {
			conceptId = fix(conceptId);

			List<BeaconConceptWithDetails> conceptDetails = null ;
			
			// I attempt caching of the whole retrieved set
			CacheLocation cacheLocation = 
					cache.searchForResultSet(
							"ConceptWithDetails", 
							conceptId, 
							new String[] { "1" }
							);

			@SuppressWarnings("unchecked")
			List<BeaconConceptWithDetails> cachedResult = (List<BeaconConceptWithDetails>)cacheLocation.getResultSet();

			if(cachedResult==null) {

				List<Graph> graphs = searchByIds(search::nodesBy, Util.list(conceptId), 100);		
				Collection<Node> nodes = Util.flatmap(Graph::getNodes, graphs);
				combineDuplicates(nodes);
				
				conceptDetails= Util.map(translator::nodeToConceptDetails, nodes);
				
				final String id = conceptId;
				conceptDetails.removeIf(d -> !d.getId().equalsIgnoreCase(id));
				
				cacheLocation.setResultSet(conceptDetails);

			} else {
				
				conceptDetails = cachedResult;
			}

			return ResponseEntity.ok(conceptDetails);

		} catch (Exception e) {
			e.printStackTrace();
			log(e);
			return ResponseEntity.ok(new ArrayList<BeaconConceptWithDetails>());
		}
	}


	public ResponseEntity<List<BeaconPredicate>> getPredicates() {
		List<BeaconPredicate> responses = new ArrayList<BeaconPredicate>(predicateRegistry.values());
		return ResponseEntity.ok(responses);		
	}

	
	public ResponseEntity<List<ExactMatchResponse>> getExactMatchesToConceptList(List<String> c) {
		try {
			c = fix(c);
			
			List<ExactMatchResponse> exactMatches = getMatchingResponses(c);
			
			return ResponseEntity.ok(exactMatches);
			
		} catch (Exception e) {
			log(e);
			return ResponseEntity.ok(new ArrayList<>());
		}
	}

	
	public ResponseEntity<List<BeaconStatement>> getStatements(
			
			List<String> s, 
			List<String> relations,
			List<String> t, 
			List<String> keywords, 
			List<String> categories,
			Integer size 
	) {
		try {
			
			s = fix(s);
			relations = fix(relations);
			t = fix(t);
			
			keywords = fix(keywords);
			categories = fix(categories);
			
			_logger.debug("Entering ControllerImpl.getStatements():\n"
					+ "\tsourceIds: '"+String.join(",",s)
					+ "',\n\trelations: '"+String.join("", relations)
					+ "',\n\ttargetIds: '"+String.join(",",t)
					+ "',\n\tkeywords: '"+keywords
					+ "',\n\tsemanticGroups: '"+categories
			);
			
			size = fixPageSize(size);
			
			List<BeaconStatement> statements = null ;
			
			// I attempt caching of the whole retrieved set
			CacheLocation cacheLocation = 
					cache.searchForResultSet(
							"Statement", 
							s.toString(), 
							new String[] {
									t.toString(),
									String.join(" ", keywords), 
									String.join(" ", categories), 
									String.join(" ", relations),
									size.toString() 
							}
					);

			@SuppressWarnings("unchecked")
			List<BeaconStatement> cachedResult = 
					(List<BeaconStatement>)cacheLocation.getResultSet();
			
			if(cachedResult==null) {			
			
				Set<String> sourceAliases = allAliases(s);
				
				_logger.debug("sourceAliases: '"+String.join(",",s)+"'");
				
				List<Graph> graphs = searchByIds(search::edgesBy, Util.list(sourceAliases), size);
				
				Collection<Node> nodes = Util.flatmap(Graph::getNodes, graphs);
				
				/* 
				 * Execute makeId on all the returned nodes for the
				 * side effect of registering their alias ndex node ids
				 */
				Util.map(translator::makeId, nodes);
				
				Collection<Edge> edges = Util.flatmap(Node::getEdges, nodes);
				
				if( ! t.isEmpty() ) {
					
					Set<String> targetAliases = getAliases(t);
					targetAliases.addAll(t);
					
					// Filter for edges with specified targets opposite to source nodes
					edges = filterByTarget( edges, sourceAliases, targetAliases );
				}
				
				edges = filterByPredicate(edges, relations);
				
				edges = filterByText(edges, keywords);

				/*
				 * We only filter targets for semantic groups if  
				 * exact target id's were NOT available for filtering? 
				 * We do this filtering last because it is more involved
				 * hence, we should do this on the smallest filtered 
				 * edge list, after all other filters are applied.
				 */
				if( t.isEmpty() )
					 edges = filterSemanticGroup( edges, sourceAliases, categories );
				
				statements = Util.map( translator::edgeToStatement, edges );
				
				// Store result in the cache
				cacheLocation.setResultSet(statements);
				
			} else {
				statements = cachedResult;
			}
			
			@SuppressWarnings("unchecked")
			// Paging workaround since nDex paging doesn't seem to work as published?
			List<BeaconStatement> page = (List<BeaconStatement>)getPage(statements, size);
			
			_logger.info("\nFinished finding statements. Current known subject to object pairs:\n {} ", translator.subjectObjectRegistry); 
			
			return ResponseEntity.ok(page);
		
		} catch (Exception e) {
			_logger.error("Exiting ControllerImpl.getStatements() ERROR: "+e.getMessage());
			log(e);
			e.printStackTrace();
			return ResponseEntity.ok(new ArrayList<>());
		}
	}

	public ResponseEntity<List<BeaconAnnotation>> getEvidence(String statementId, List<String> keywords, Integer size) {
		try {
		
			statementId = fix(statementId);
			keywords = fix(keywords);
			size = fixPageSize(size);
			
			if(statementId.startsWith(Translator.NDEX_NS)) {
				statementId = statementId.replaceAll("^"+Translator.NDEX_NS, "");
			}
			
			String[] half = statementId.split("_", 2);
			String conceptId = half[0];
			Long statement = Long.valueOf(half[1]);

			List<Graph> graphs = searchByIds(search::edgesBy, Util.list(conceptId), size);
			
			Collection<Edge> relatedEdges = Util.flatmap(Graph::getEdges, graphs);
			
			Predicate<Edge> wasRequested = e -> e.getId().equals(statement);
			List<Edge> maybeEdge = Util.filter(wasRequested, relatedEdges);
			
			if (maybeEdge.size() == 1) {
				
				List<BeaconAnnotation> evidence = new ArrayList<BeaconAnnotation>();

				/*
				 *  Insert the current Graph network identifier 
				 *  as one piece of "evidence" alongside 
				 *  any other discovered citation evidence
				 */
				BeaconAnnotation networkEvidence = new BeaconAnnotation();
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
	
	public ResponseEntity<List<BeaconConceptCategory>> getCategories() {
		List<BeaconConceptCategory> types = new ArrayList<BeaconConceptCategory>();
		
		// Hard code some known types... See Translator.makeSemGroup()
		types.add(createBeaconConceptCategoryFromUMLS("GENE"));
		types.add(createBeaconConceptCategoryFromUMLS("CHEM"));
		types.add(createBeaconConceptCategoryFromUMLS("DISO"));
		types.add(createBeaconConceptCategoryFromUMLS("PHYS"));
		types.add(createBeaconConceptCategoryFromUMLS("ANAT"));
		types.add(createBeaconConceptCategoryFromUMLS("PROC"));
		types.add(createBeaconConceptCategoryFromUMLS("OBJC"));
		
		
		// doesn't seem to exist in Biolink
//		BeaconConceptCategory LIVB_Type = new BeaconConceptCategory();
//		LIVB_Type.setId(ontology.umlsToBiolinkCategory("LIVB"));
//		types.add(LIVB_Type);
//		
		
		return ResponseEntity.ok(types);
    }
	
	private BeaconConceptCategory createBeaconConceptCategoryFromUMLS(String umls) {
		BeaconConceptCategory category = new BeaconConceptCategory();
		String biolinkName = ontology.umlsToBiolinkCategory(umls);
		category.setId(biolinkName);
		category.setCategory(biolinkName);
		category.setLocalId(umls);
		category.setLocalCategory("UMLSSG:" + umls);
		return category;
	}


	public ResponseEntity<List<BeaconKnowledgeMapStatement>> getKnowledgeMap() {
		List<BeaconKnowledgeMapStatement> knowledgeMapStatements = new ArrayList<BeaconKnowledgeMapStatement>();
		
		for (Map.Entry<StatementTriple, Integer> entry : translator.subjectObjectRegistry.entrySet()) {
			StatementTriple triple = entry.getKey();
			BeaconKnowledgeMapStatement statement = new BeaconKnowledgeMapStatement();
			
			BeaconKnowledgeMapSubject subject = new BeaconKnowledgeMapSubject();
			subject.setCategory(triple.getSubject());
			subject.setPrefixes(ontology.getBiolinkCategoryIdPrefixes(triple.getSubject()));
			statement.setSubject(subject);
			
			BeaconKnowledgeMapPredicate predicate = new BeaconKnowledgeMapPredicate();
			//TODO: needs updating once biolink predicates are being generated more appropriately
			// Predicate in the triple are currently being stored as NDEX:ndex_relation_name for extra logging information
			// Here we need to convert it to a biolink label for public kmap reporting purposes
			String predicateRelationship = ontology.removeSnakeCase(triple.getPredicate());
			if (predicateRelationship.startsWith(Translator.NDEX_NS)) {
				predicateRelationship = predicateRelationship.substring(Translator.NDEX_NS.length());
			}
			String biolinkName = ontology.predToBiolinkEdgeLabel(predicateRelationship);
			predicate.setRelation(biolinkName);
			statement.setPredicate(predicate);

			BeaconKnowledgeMapObject object = new BeaconKnowledgeMapObject();
			object.setCategory(triple.getObject());
			object.setPrefixes(ontology.getBiolinkCategoryIdPrefixes(triple.getObject()));
			statement.setObject(object);
			
			
			statement.setSubject(subject);
			statement.setPredicate(predicate);
			statement.setObject(object);
			statement.setFrequency(entry.getValue());
			statement.setDescription(triple.getSubject() + " - " + predicateRelationship + " - " + triple.getObject());
			
			knowledgeMapStatements.add(statement);
		}
		
		return ResponseEntity.ok(knowledgeMapStatements);
		
	}
}
