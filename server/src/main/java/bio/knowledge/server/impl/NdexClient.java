package bio.knowledge.server.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import bio.knowledge.server.json.AdvancedQuery;
import bio.knowledge.server.json.Aspect;
import bio.knowledge.server.json.Attribute;
import bio.knowledge.server.json.BasicQuery;
import bio.knowledge.server.json.Edge;
import bio.knowledge.server.json.Network;
import bio.knowledge.server.json.NetworkList;
import bio.knowledge.server.json.Node;
import bio.knowledge.server.json.SearchString;


/**
 * Queries NDEx for networks and subnets.
 * 
 * @author Meera Godden
 *
 */
@Service
public class NdexClient {

	
	private RestTemplate rest;
	private HttpHeaders headers;
	
	private static final String NDEX = "http://www.ndexbio.org/v2";
	private static final String NETWORK_SEARCH = NDEX + "/search/network?start={start}&size={size}";
	private static final String BASIC_QUERY = NDEX + "/search/network/{networkId}/query";
	private static final String ADVANCED_QUERY = NDEX + "/search/network/{networkId}/advancedquery";
	
	
	public NdexClient() {
		rest = new RestTemplate();
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
	}
	
	private void log(Exception e) {
		System.err.println(e.getClass() + ": " + e.getMessage());
	}
	
	public NetworkList searchNetworks(SearchString searchString, int pageNumber, int pageSize) {	
		
		try {
			HttpEntity<SearchString> request = new HttpEntity<>(searchString, headers);
			NetworkList networks = rest.postForObject(NETWORK_SEARCH, request, NetworkList.class, pageNumber, pageSize);
			
			return networks;
		
		} catch (Exception e) {
			log(e);
			return new NetworkList();
		}
	}
	
	public CompletableFuture<Network> queryNetwork(String networkId, BasicQuery nodeSearch) {	

		HttpEntity<BasicQuery> request = new HttpEntity<>(nodeSearch, headers);
		
		CompletableFuture<Network> future = CompletableFuture.supplyAsync(() -> {
		
//		ObjectMapper mapper = new ObjectMapper();
			try {
				
				
				Aspect[] aspects = rest.postForObject(BASIC_QUERY, request, Aspect[].class, networkId);
				
//				HashMap[] h = rest.postForObject(BASIC_QUERY, request, HashMap[].class, networkId);
//				
//				// TODO: update so not hardcoded in
//
//				Node[] nodes = mapper.convertValue(h[3].get("nodes"), Node[].class);
//				Edge[] edges = mapper.convertValue(h[2].get("edges"), Edge[].class);
//				Attribute[] nodeAttributes = mapper.convertValue(h[4].get("nodeAttributes"), Attribute[].class);
//				Attribute[] edgeAttributes = mapper.convertValue(h[5].get("edgeAttributes"), Attribute[].class);
//				
//				// set ndexStatus to networkId
//				Aspect[] aspects = new Aspect[1];
//				aspects[0] = new Aspect();
//				aspects[0].setNdexStatus(networkId);
//				aspects[0].setNodes(nodes);
//				aspects[0].setEdges(edges);
//				aspects[0].setNodeAttributes(nodeAttributes);
//				aspects[0].setEdgeAttributes(edgeAttributes);
				
				for (int i = 0; i < aspects.length; i++) {
					aspects[i].setNdexStatus(networkId); 
				}
				
				
				Network network = new Network();
				network.setData(aspects);
				
				return network;
				
			} catch (Exception e) {
				log(e);
				return new Network();
			}
			
		});
		
		return future;
	}
	
	public Network advancedQueryNetwork(String networkId, AdvancedQuery nodeSearch) {	

		try {
			HttpEntity<AdvancedQuery> request = new HttpEntity<>(nodeSearch, headers);
			Network network = rest.postForObject(ADVANCED_QUERY, request, Network.class, networkId);
			return network;
		
		} catch (Exception e) {
			log(e);
			return new Network();
		}
	
	}

}
