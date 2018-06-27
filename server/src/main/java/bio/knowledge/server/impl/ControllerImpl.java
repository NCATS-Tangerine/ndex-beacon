package bio.knowledge.server.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

import bio.knowledge.ontology.BiolinkTerm;
import bio.knowledge.server.impl.Cache.CacheLocation;
import bio.knowledge.server.json.Attribute;
import bio.knowledge.server.json.BasicQuery;
import bio.knowledge.server.json.Edge;
import bio.knowledge.server.json.Network;
import bio.knowledge.server.json.NetworkId;
import bio.knowledge.server.json.NetworkList;
import bio.knowledge.server.json.Node;
import bio.knowledge.server.json.SearchString;
import bio.knowledge.server.model.BeaconConcept;
import bio.knowledge.server.model.BeaconConceptCategory;
import bio.knowledge.server.model.BeaconConceptWithDetails;
import bio.knowledge.server.model.BeaconKnowledgeMapStatement;
import bio.knowledge.server.model.BeaconPredicate;
import bio.knowledge.server.model.BeaconStatement;
import bio.knowledge.server.model.BeaconStatementCitation;
import bio.knowledge.server.model.BeaconStatementWithDetails;
import bio.knowledge.server.model.ExactMatchResponse;
import bio.knowledge.server.ontology.NdexConceptCategoryService;
import bio.knowledge.server.ontology.OntologyService;

@Service
public class ControllerImpl {

	private static Logger _logger = LoggerFactory.getLogger(ControllerImpl.class);
	
	@Autowired OntologyService ontology;
	@Autowired private Cache cache;
	@Autowired private SearchBuilder search;
	@Autowired private NdexClient ndex;
	@Autowired private Translator translator;
	@Autowired private AliasNamesRegistry aliasRegistry;
	@Autowired private PredicatesRegistry predicateRegistry;
	@Autowired private KnowledgeMapRegistry knowledgeMapRegistry;

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
	
	private static List<String> makeNonNull(List<String> stringList ) {
		if (stringList != null) {
			stringList.removeIf(s -> s == null || s.isEmpty());
			return stringList;
		} else {
			return new ArrayList<String>();
		}
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
	
	/**
	 * Performs query on ndex networks (sends HTTP requests)
	 * Caches query on luceneSearch 
	 * @param makeJson
	 * @param luceneSearch
	 * @param size
	 * @return graph returned from given luceneSearch
	 */
	private List<Graph> search(Function<String, BasicQuery> makeJson, String luceneSearch, String queryType) {
		
		CacheLocation cacheLocation = 
				cache.searchForResultSet(
						"Graph", 
						luceneSearch, 
						new String[] { luceneSearch }
				);

		@SuppressWarnings("unchecked")
		List<Graph> cachedResult = 
				(List<Graph>)cacheLocation.getResultSet();
		
		List<Graph> graphs = cachedResult;
		if(graphs==null) {
		
			SearchString networkSearch = search.networksBy(restrictQuery(luceneSearch));
			BasicQuery subnetQuery = makeJson.apply(luceneSearch);
			
			NetworkList networks = ndex.searchNetworks(networkSearch);
			
			List<String> networkIds = Util.map(NetworkId::getExternalId, networks.getNetworks());

			List<CompletableFuture<Network>> futures = new ArrayList<>();
			for (String networkId : networkIds) {
				futures.add(ndex.queryNetwork(networkId, subnetQuery, queryType));
			}
			
			List<Network> subnetworks = new ArrayList<>();
			for (CompletableFuture<Network> future : futures) {
				try {
					subnetworks.add(future.get(TIMEOUT, TIMEUNIT));
				} catch (InterruptedException | ExecutionException | TimeoutException e) {
					log(e);
				}
			}
			
			graphs = Util.map(Graph::new, subnetworks);
			
			cacheLocation.setResultSet(graphs);
			
		} 
		
		return graphs;
	}
	
	private List<Graph> searchByIds(Function<String, BasicQuery> makeJson, List<String> c, String queryType) {
		
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
				
				CompletableFuture<Network> network = get(ndex.queryNetwork(networkId, subnetQuery, queryType));
				
				futures.add(network);
			
			} else if (Node.isCurie(conceptId)){
				realCuries.add(conceptId);
			}
		}
		
		if (!realCuries.isEmpty()) {
		
			List<String> phrases = Util.map(search::phrase, realCuries);
			String luceneSearch = search.or(phrases);
			
			List<Graph> results = search(makeJson, luceneSearch, queryType);
			
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
			
			List<String> curieToSearch = Util.list(curie);
			
			List<Graph> graphs = searchByIds( search::nodesBy, curieToSearch, NdexClient.QUERY_FOR_NODE_MATCH);
			Collection<Node> nodes = Util.flatmap( Graph::getNodes, graphs );
			
			if (nodes.size() == 0) {
				match.setWithinDomain(false);
			} else {
				match.setWithinDomain(true);
			}
			
			Set<String> aliases = getAliasesByNodes(curieToSearch, nodes);
			
			List<String> curies  = Util.filter(Node::isCurie, aliases);
			Set<String> curiesSet = addCachedAliases(curies);
			curiesSet.remove(curie);
			List<String> exactMatches = new ArrayList<String>(curiesSet);
			match.setHasExactMatches(exactMatches);
			
			response.add(match);
		}
		
		return response;
	}
	
	/**
	 * Returns aliases by searching through returned nodes for the "alias" attribute and checking that the node
	 * represents the curie we are looking for
	 */
	private Set<String> getAliasesByNodes(List<String> curies, Collection<Node> nodes) {
		
		Set<String> aliases = new HashSet<String>();
		
		for (Node node : nodes) {
			for (Attribute attribute : node.getAttributes()) {
				if (attribute.getName().equals("alias") && (nodeOrAliasMatchesCuries(curies, node, attribute))) {
					for (String value : attribute.getValues()) {
						aliases.add(value.trim());
					}
				}
			}
		}
		
		return aliases;
	
	}
	
	/**
	 * Search on ndex may return more than one node, one which matches the search curie in its node ID (getRepresents)
	 * or in the alias values
	 * @param curie
	 * @param node
	 * @param attribute - should be an "alias" attribute
	 * @return true if current node has node ID or alias value that matches search curie, false otherwise
	 */
	private boolean nodeOrAliasMatchesCuries(List<String> curies, Node node, Attribute attribute) {
		
		assert(attribute.getName().equals("alias"));
		
		if (curies.contains(node.getRepresents()))
			return true;
		
		List<String> attributeAliases = attribute.getValues();
		for (String attributeAlias : attributeAliases) {
			if (curies.contains(attributeAlias)) {
				return true;
			}
		}
		
		return false;
	}

	/*
	 * This only returns the aliases of the members of the 'c' list of input identifiers
	 * but not directly the 'c' identifiers themselves nor their locally cached related ids
	 */
	private Set<String> getAliases( List<String> c ) {
		
		List<Graph> graphs = searchByIds( search::nodesBy, c, NdexClient.QUERY_FOR_NODE_MATCH);
		
		Collection<Node> nodes = Util.flatmap( Graph::getNodes, graphs );
		
		Set<String> aliases = getAliasesByNodes(c, nodes);
		
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
		
		Predicate<BeaconConcept> hasType = n -> !Collections.disjoint(types, n.getCategories());
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

	public List<?> getPage(List<?> items, Integer size) {
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
			
			keywords = makeNonNull(keywords);
			categories = makeNonNull(categories);
			size = fixPageSize(size);
			
			List<BeaconConcept> concepts = null ;
			
			String joinedKeywords = String.join(" ", keywords);
			String luceneSearch = search.startsWith(joinedKeywords);
			List<Graph> graphs = search(search::nodesBy, luceneSearch, NdexClient.QUERY_FOR_NODE_AND_EDGES);		
			Collection<Node> nodes = Util.flatmap(Graph::getNodes, graphs);
			combineDuplicates(nodes);
			addToKmapAndPredMetadata(nodes);
			Util.map(translator::makeId, nodes);
			
			concepts = Util.map(translator::nodeToConcept, nodes);
			List<BeaconConcept> ofType = filterSemanticGroup(concepts, categories);
			
			@SuppressWarnings("unchecked")
			List<BeaconConcept> page = (List<BeaconConcept>)getPage(ofType, size);
			return ResponseEntity.ok(page);
		
		} catch (Exception e) {
			log(e);
			return ResponseEntity.ok(new ArrayList<BeaconConcept>());
		}
	}


	// TODO: method may throw assert error - not sure if there will be more than one returned
	// and in what circumstances
	public ResponseEntity<BeaconConceptWithDetails> getConceptDetails(String conceptId) {
		try {
			conceptId = fix(conceptId);
	
			List<Graph> graphs = searchByIds(search::nodesBy, Util.list(conceptId), NdexClient.QUERY_FOR_NODE_MATCH);		
			Collection<Node> nodes = Util.flatmap(Graph::getNodes, graphs);
			combineDuplicates(nodes);
			
			List<BeaconConceptWithDetails> conceptDetails= Util.map(translator::nodeToConceptDetails, nodes);
			
			final String id = conceptId;
			conceptDetails.removeIf(d -> !d.getId().equalsIgnoreCase(id));
			
			assert(conceptDetails.size() == 1);

			return ResponseEntity.ok(conceptDetails.get(0));

		} catch (Exception e) {
			e.printStackTrace();
			log(e);
			return ResponseEntity.ok(new BeaconConceptWithDetails());
		}
	}

	public ResponseEntity<List<ExactMatchResponse>> getExactMatchesToConceptList(List<String> c) {
		try {
			c = makeNonNull(c);
			
			List<ExactMatchResponse> exactMatches = getMatchingResponses(c);
			
			return ResponseEntity.ok(exactMatches);
			
		} catch (Exception e) {
			log(e);
			return ResponseEntity.ok(new ArrayList<>());
		}
	}

	public ResponseEntity<List<BeaconStatement>> getStatements(
			List<String> s, 
			String edgeLabel, 
			String relation,
			List<String> t,
			List<String> keywords, 
			List<String> categories, 
			Integer size
	) {
		try {
			s = makeNonNull(s);
			edgeLabel = fix(edgeLabel);
			relation = fix(relation);
			t = makeNonNull(t);
			keywords = makeNonNull(keywords);
			categories = makeNonNull(categories);
			size = fixPageSize(size);
			
			_logger.debug("Entering ControllerImpl.getStatements():\n"
					+ "\tsourceIds: '"+String.join(",",s)
					+ "',\n\tedgeLabel: '"+edgeLabel
					+ "',\n\trelation: '"+relation
					+ "',\n\ttargetIds: '"+String.join(",",t)
					+ "',\n\tkeywords: '"+keywords
					+ "',\n\tsemanticGroups: '"+categories
			);
			
			List<BeaconStatement> statements = null;
			
			Set<String> sourceAliases = allAliases(s);
			_logger.debug("sourceAliases: '"+String.join(",",s)+"'");
			
			List<Graph> graphs = searchByIds(search::edgesBy, Util.list(sourceAliases), NdexClient.QUERY_FOR_NODE_AND_EDGES);
			
			Collection<Node> nodes = Util.flatmap(Graph::getNodes, graphs);
				
			Util.map(translator::makeId, nodes);
			
			Collection<Edge> edges = Util.flatmap(Node::getEdges, nodes);
			
			if( ! t.isEmpty() ) {
				
				Set<String> targetAliases = getAliases(t);
				targetAliases.addAll(t);
				
				// Filter for edges with specified targets opposite to source nodes
				edges = filterByTarget( edges, sourceAliases, targetAliases );
			}
			
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
			
			statements = filterByRelationAndEdgeLabel(statements, relation, edgeLabel);
			
			@SuppressWarnings("unchecked")
			// Paging workaround since nDex paging doesn't seem to work as published?
			List<BeaconStatement> page = (List<BeaconStatement>)getPage(statements, size);
			
			return ResponseEntity.ok(page);
		
		} catch (Exception e) {
			_logger.error("Exiting ControllerImpl.getStatements() ERROR: "+e.getMessage());
			log(e);
			e.printStackTrace();
			return ResponseEntity.ok(new ArrayList<>());
		}
	}
	
	
	private List<BeaconStatement> filterByRelationAndEdgeLabel(List<BeaconStatement> statements, String relation,
			String edgeLabel) {
		
		if (relation.isEmpty() && edgeLabel.isEmpty()) {
			return statements;
		}
		
		Predicate<BeaconStatement> matches; 
		
		if (edgeLabel.isEmpty()) {
			matches = s -> relation.equals(s.getPredicate().getRelation());
		} else if (relation.isEmpty()) {
			matches = s -> edgeLabel.equals(s.getPredicate().getEdgeLabel());
		} else {
			matches = s -> relation.equals(s.getPredicate().getRelation()) 
					       && edgeLabel.equals(s.getPredicate().getEdgeLabel());
		}
		
		List<BeaconStatement> matchingStatements = Util.filter(matches, statements);
		return matchingStatements;
	}

	public ResponseEntity<BeaconStatementWithDetails> getStatementDetails(String statementId, List<String> keywords, Integer size) {
		try {
			statementId = fix(statementId);
			keywords = makeNonNull(keywords);
			size = fixPageSize(size);

			String[] splitIds = statementId.split("_", 2);
			String networkAndSubjectId = splitIds[0];
			String networkId = networkAndSubjectId.replaceFirst(Translator.NDEX_NS, "").split("#")[0];
			Long objectId = Long.valueOf(splitIds[1]);

			List<Graph> graphs = searchByIds(search::edgesBy, Util.list(networkAndSubjectId), NdexClient.QUERY_FOR_NODE_AND_EDGES);

			Collection<Edge> relatedEdges = Util.flatmap(Graph::getEdges, graphs);

			Predicate<Edge> wasRequested = e -> e.getId().equals(objectId);
			List<Edge> maybeEdge = Util.filter(wasRequested, relatedEdges);

			assert(maybeEdge.size() == 1);
			if (maybeEdge.size() == 1) {
				BeaconStatementWithDetails result = translator.edgeToStatementDetails(maybeEdge.get(0), networkId);
				filterByKeywords(result, keywords);
				return ResponseEntity.ok(result);
			} else {
				return ResponseEntity.ok(null);
			}

		} catch (Exception e) {
			return ResponseEntity.ok(null);
		}
	}
	
	private void filterByKeywords(BeaconStatementWithDetails result, List<String> keywords) {
		if (keywords.isEmpty()) return;

		List<BeaconStatementCitation> evidence = result.getEvidence();

		if (!(evidence.isEmpty())) {
			Predicate<BeaconStatementCitation> hasKeywords = c -> containsAll(c.getName(), keywords);
			result.setEvidence(Util.filter(hasKeywords, evidence));
		}
	}

	public ResponseEntity<List<BeaconConceptCategory>> getCategories() {
		List<BeaconConceptCategory> types = new ArrayList<BeaconConceptCategory>();
		
		for (BiolinkTerm term : NdexConceptCategoryService.POSSIBLE_NDEX_CATEGORIES) {
			BeaconConceptCategory category = new BeaconConceptCategory();
			category.setCategory(term.getLabel());
			category.setId(term.getCurie());
			category.setFrequency(-1);
			category.setDescription(term.getDefinition());
			category.setUri(term.getIri());
			types.add(category);
		}
	
		return ResponseEntity.ok(types);
    }
	
	public ResponseEntity<List<BeaconPredicate>> getPredicates() {
		List<BeaconPredicate> responses = new ArrayList<BeaconPredicate>(predicateRegistry.values());
		return ResponseEntity.ok(responses);		
	}

	public ResponseEntity<List<BeaconKnowledgeMapStatement>> getKnowledgeMap() {
		
		List<BeaconKnowledgeMapStatement> responses = 
				new ArrayList<BeaconKnowledgeMapStatement>(knowledgeMapRegistry.values());

		return ResponseEntity.ok(responses);
	}
	
	/**
	 * Adds information to /predicates and /kmap endpoints through the translator::edgeToStatement method 
	 * This results in extra computation to create statements that are never used, so it may be useful
	 * to save the information somewhere in the future
	 * @param nodes
	 */
	private void addToKmapAndPredMetadata(Collection<Node> nodes) {
		Collection<Edge> edges = Util.flatmap(Node::getEdges, nodes);
		Util.map( translator::edgeToStatement, edges );
	}
}
