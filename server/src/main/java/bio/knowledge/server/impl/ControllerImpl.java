package bio.knowledge.server.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
import bio.knowledge.server.transl.Graph;
import bio.knowledge.server.transl.SearchBuilder;
import bio.knowledge.server.transl.Translator;

@Service
public class ControllerImpl {
					
	// todo: handle bad input eg don't ask ndex if blank
		
	// todo: paging
	// todo: stop once found enough

//	 todo: semantic group, details, synonyms, alias for exactmatch, linkedtypes, predicateId
	
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
	
	
	private boolean isNdexId(String conceptId) {
		
		try {
			if (conceptId.length() >= 38) {
				UUID.fromString(conceptId.substring(0, 36));
				return conceptId.charAt(36) == ':';
			}
		
		} catch (IllegalArgumentException e) {}
		
		return false;
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
	
	private List<Graph> search(Function<String, BasicQuery> makeJson, Function<String, String> makeLucene,  String rawString, int pageNumber, int pageSize) {
		
		String luceneSearch = makeLucene.apply(rawString);
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
	
	private List<Graph> searchById(Function<String, BasicQuery> makeJson, String conceptId) {
		
		List<Graph> graphs = new ArrayList<>();
		
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
			graphs = search(makeJson, searchBuilder::phrase, conceptId, 0, PAGE_SIZE);
		}
		
		return graphs;
	}
	
	
	public ResponseEntity<List<InlineResponse2001>> getConceptDetails(String conceptId) {
		
		conceptId = fix(conceptId);
			
		List<Graph> graphs = searchById(searchBuilder::nodesBy, conceptId);		
		Collection<Node> nodes = Util.flatmapList(Graph::getNodes, graphs);
		List<InlineResponse2001>  conceptDetails = Util.map(translator::nodeToConceptDetails, nodes);
		
		return ResponseEntity.ok(conceptDetails);
	}

	// todo: get label for cases like 55c84fa4-01b4-11e5-ac0f-000c29cb28fb AKT (debug 1)
	// todo: ignore null named?
	public ResponseEntity<List<InlineResponse2002>> getConcepts(
			String keywords, String semgroups, Integer pageNumber, Integer pageSize) {
		
		keywords = fix(keywords);
		semgroups = fix(semgroups);
		pageNumber = fix(pageNumber) - 1;
		pageSize = fix(pageSize);
		
		List<Graph> graphs = search(searchBuilder::nodesBy, searchBuilder::startsWith, keywords, pageNumber, pageSize);		
		Collection<Node> nodes = Util.flatmapList(Graph::getNodes, graphs);		
		List<InlineResponse2002> concepts = Util.map(translator::nodeToConcept, nodes);
		
		return ResponseEntity.ok(concepts);
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
		
		Collection<Edge> relatedEdges = getEdges(conceptId);
		
		Predicate<Edge> wasRequested = e -> e.getId().equals(statement);
		Edge edge = Util.filter(wasRequested, relatedEdges).get(0);
		List<Citation> citations = edge.getCitations(); // todo: also put citations as evidence (eg id but no text?)
		
		List<InlineResponse2004> evidence = citations == null? new ArrayList<>() : Util.map(translator::citationToEvidence, citations);
		
		return ResponseEntity.ok(evidence);
	}
	

	public ResponseEntity<List<String>> getExactMatchesToConcept(String conceptId) {
		// TODO Auto-generated method stub
		return null;
	}
	

	public ResponseEntity<List<String>> getExactMatchesToConceptList(List<String> c) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public Collection<Edge> getEdges(String conceptId) {
		
		List<Graph> graphs = searchById(searchBuilder::edgesBy, conceptId);		
		Collection<Edge> edges = Util.flatmapList(Graph::getEdges, graphs);
		return edges;
	}
	
	
	// todo: in this and get concepts method, handle outside curies (eg HGNC) as (exactmatch) names
	public ResponseEntity<List<InlineResponse2003>> getStatements(
			List<String> c, Integer pageNumber, Integer pageSize, String keywords, String semgroups) {
		
		c = fix(c);
		pageNumber = fix(pageNumber) - 1;
		pageSize = fix(pageSize);
		keywords = fix(keywords); // todo: handle
		semgroups = fix(semgroups);
		
		List<Edge> edges = Util.flatmapList(this::getEdges, c);  // todo: nicer naming
		List<InlineResponse2003> statements = Util.map(translator::edgeToStatement, edges);
		return ResponseEntity.ok(statements);
	}

	
	public ResponseEntity<List<InlineResponse200>> linkedTypes() {
		// TODO Auto-generated method stub
		return null;
	}

}
