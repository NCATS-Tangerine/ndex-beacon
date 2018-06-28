package bio.knowledge.server.impl;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import bio.knowledge.server.json.AdvancedQuery;
import bio.knowledge.server.json.Aspect;
import bio.knowledge.server.json.BasicQuery;
import bio.knowledge.server.json.Network;
import bio.knowledge.server.json.NetworkSummary;
import bio.knowledge.server.json.NetworkList;
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
	
	private static final Integer PAGE_NUMBER = 0;
	
	public static final String NDEX = "http://www.ndexbio.org/v2";
	public static final String NETWORK_SEARCH = NDEX + "/search/network?start={start}&size={size}";
	public static final String QUERY_FOR_NODE_MATCH = NDEX + "/search/network/{networkId}/interconnectquery";
	public static final String QUERY_FOR_NODE_AND_EDGES = NDEX + "/search/network/{networkId}/query";
	public static final String ADVANCED_QUERY = NDEX + "/search/network/{networkId}/advancedquery";
	public static final String NETWORK_SUMMARY = NDEX + "/network/{networkId}/summary";
	
	private static final int NETWORK_SEARCH_SIZE = 100;

	public NdexClient() {
		rest = new RestTemplate();
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
	}
	
	private void log(Exception e) {
		System.err.println(e.getClass() + ": " + e.getMessage());
	}
	
	public NetworkList searchNetworks(SearchString searchString) {	
		
		try {
			HttpEntity<SearchString> request = new HttpEntity<>(searchString, headers);
			//@SuppressWarnings("unused")
			HashMap networkHash = rest.postForObject(NETWORK_SEARCH, request, HashMap.class, PAGE_NUMBER, NETWORK_SEARCH_SIZE);
			NetworkList networks = rest.postForObject(NETWORK_SEARCH, request, NetworkList.class, PAGE_NUMBER, NETWORK_SEARCH_SIZE);
			
			return networks;
		
		} catch (Exception e) {
			log(e);
			return new NetworkList();
		}
	}
	
	public CompletableFuture<Network> queryNetwork(NetworkSummary networkSummary, BasicQuery nodeSearch, String queryType) {	

		HttpEntity<BasicQuery> request = new HttpEntity<>(nodeSearch, headers);
		
		CompletableFuture<Network> future = CompletableFuture.supplyAsync(() -> {
		
			try {
				
				String networkId = networkSummary.getNetworkId();
				Aspect[] aspects = rest.postForObject(queryType, request, Aspect[].class, networkId);

//				//HashMap[] h = rest.postForObject(BASIC_QUERY, request, HashMap[].class, networkId);
//				for (int i = 0; i < aspects.length; i++) {
//					aspects[i].setNdexStatus(networkId); 
//				}
				
				
				Network network = new Network();
				network.setNetworkId(networkId);
				network.setProperties(networkSummary.getProperties());
				network.setData(aspects);
				
				return network;
				
			} catch (Exception e) {
				log(e);
				return new Network();
			}
			
		});
		
		return future;
	}
	
	public NetworkSummary queryNetworkId(String networkId) {
		try {
			CompletableFuture<NetworkSummary> future = CompletableFuture.supplyAsync(() -> {
				NetworkSummary networkSummary = rest.getForObject(NETWORK_SUMMARY, NetworkSummary.class, networkId);
				return networkSummary;
			});
			
			return future.get();
		} catch (Exception e) {
			log(e);
			return new NetworkSummary();
		}
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
