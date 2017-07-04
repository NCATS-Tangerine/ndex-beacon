package bio.knowledge.server.impl;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import bio.knowledge.server.json.Aspect;
import bio.knowledge.server.json.Network;
import bio.knowledge.server.json.Edge;
import bio.knowledge.server.json.NetworkId;
import bio.knowledge.server.json.NetworkList;
import bio.knowledge.server.json.NetworkQuery;
import bio.knowledge.server.json.Node;
import bio.knowledge.server.json.SearchString;
import bio.knowledge.server.model.InlineResponse200;
import bio.knowledge.server.model.InlineResponse2001;
import bio.knowledge.server.model.InlineResponse2002;
import bio.knowledge.server.model.InlineResponse2003;
import bio.knowledge.server.model.InlineResponse2004;
import bio.knowledge.server.transl.Graph;
import bio.knowledge.server.transl.Search;
import bio.knowledge.server.transl.Translate;

@Service
public class ControllerImpl {
	
	// todo: use batch?
	
	//todo: use wildcards to avoid just matching network name/description?
	
	// todo: start at zeroeth block
		
	// todo: handle bad input eg don't ask ndex if blank
	// esp on getconcept where splitting
	
	// todo: factor search details elsewhere
	
	// todo: paging
	/// todo: r field
	
	@Autowired
	private NdexService ndex;
	
	private static final long TIMEOUT = 1;
	private static final TimeUnit TIMEUNIT = TimeUnit.MINUTES;
	
	// todo: use futures
	// todo: cache?
	
	private static Integer fix(Integer integer) {
		return integer == null? 1 : integer;
	}
	
	private static String fix(String string) {
		return string == null? "" : string;
	}
	
	private static List<String> fix(List<String> strings) {
		return Util.map(ControllerImpl::fix, strings);
	}
	
	// todo: delete unneeded, remove comments, remove prints, complete todos
	
	public ResponseEntity<List<InlineResponse2001>> getConceptDetails(String conceptId) {
		
//		conceptId = fix(conceptId);
//		
//		String[] half = conceptId.split(":");
//		String networkId = half[0];
//		String nodeId = half[1];
//		
//		String keywords = "id:" + nodeId;
//		AspectList aspects = ndex.queryNetwork(keywords, networkId);
//		// todo: ...
//		
		return null;
	}

	// todo: add synonyms
	// todo: get label for cases like 55c84fa4-01b4-11e5-ac0f-000c29cb28fb AKT (debug 1)
	// todo: ignore null named?
	public ResponseEntity<List<InlineResponse2002>> getConcepts(
			String keywords, String semgroups, Integer pageNumber, Integer pageSize) {
		
		keywords = fix(keywords);
		semgroups = fix(semgroups);
		pageNumber = fix(pageNumber);
		pageSize = fix(pageSize);
		
		SearchString searchObject1 = Search.networksMatchingAny(keywords);
		NetworkList networks = ndex.searchNetworks(searchObject1, pageNumber, pageSize);
		List<String> networkIds = Util.map(NetworkId::getExternalId, networks.getNetworks());
		
		
		NetworkQuery searchObject2 = Search.nodesMatchingAny(keywords);
		Function<String, Network> searchNetwork = Util.curryRight(ndex::queryNetwork, searchObject2);
		List<Network> subnetworks = Util.map(searchNetwork, networkIds);
		
		List<Aspect> aspects = Util.flatmap(Network::getData, subnetworks);		
		List<Node> nodes = Util.flatmap(Aspect::getNodes, aspects);
		List<InlineResponse2002> concepts = Util.map(Translate::nodeToConcept, nodes);
		
		return ResponseEntity.ok(concepts);
	}

	public ResponseEntity<List<InlineResponse2004>> getEvidence(String statementId, String keywords, Integer pageNumber,
			Integer pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseEntity<List<String>> getExactMatchesToConcept(String conceptId) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseEntity<List<String>> getExactMatchesToConceptList(List<String> c) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// todo: paging
	private List<InlineResponse2003> getStatements(String conceptId) {
				
		String[] half = conceptId.split(":", 2);
		String networkId = half[0];
		String nodeId = half[1];
		
		NetworkQuery searchObject = Search.edgesByNodeId(nodeId); // todo: use Search object instead
		Network network = ndex.queryNetwork(networkId, searchObject);
		
		Graph graph = new Graph(network);
		Collection<Edge> edges = graph.getEdges();
		List<InlineResponse2003> concepts = Util.map(Translate::edgeToStatement, edges);
		
		return concepts;
	}
	
	// todo: in this and get concepts method, handle outside curies (eg HGNC) as (exactmatch) names
	public ResponseEntity<List<InlineResponse2003>> getStatements(
			List<String> c, Integer pageNumber, Integer pageSize, String keywords, String semgroups) {
		
		c = fix(c);
		pageNumber = fix(pageNumber);
		pageSize = fix(pageSize);
		keywords = fix(keywords); // todo: handle
		semgroups = fix(semgroups);
		
		List<InlineResponse2003> concepts = Util.flatmapList(this::getStatements, c);
		
		return ResponseEntity.ok(concepts);
		
	}

	public ResponseEntity<List<InlineResponse200>> linkedTypes() {
		// TODO Auto-generated method stub
		return null;
	}

}
