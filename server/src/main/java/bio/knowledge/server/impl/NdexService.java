package bio.knowledge.server.impl;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import bio.knowledge.server.json.Network;
import bio.knowledge.server.json.NetworkList;
import bio.knowledge.server.json.NetworkQuery;
import bio.knowledge.server.json.SearchString;

//@Async
@Service
public class NdexService {
	
	// todo: use r to get synonyms
	// todo: paging...
	
	// todo: timeout...
	// todo: test with nulls
	private RestTemplate rest;
	private HttpHeaders headers;
	
	private static final String NDEX = "http://www.ndexbio.org/v2";
	private static final String NETWORK_SEARCH = NDEX + "/search/network?start={start}&size={size}";
	private static final String NODE_SEARCH = NDEX + "/search/network/{networkId}/query";
	
	public NdexService() {
		rest = new RestTemplate();
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
	}
	
	private void log(Exception e) {
		System.err.println(e.getClass() + ": " + e.getMessage());
	}
	
	public NetworkList searchNetworks(SearchString searchString, int pageNumber, int pageSize) {	
	//public CompletableFuture<NetworkList> findNetworks(String keywords, int pageNumber, int pageSize) {	
		
		try {
			HttpEntity<SearchString> request = new HttpEntity<>(searchString, headers);
			NetworkList networks = rest.postForObject(NETWORK_SEARCH, request, NetworkList.class, pageNumber, pageSize);
			return networks; //return CompletableFuture.completedFuture(networks);
		
		} catch (Exception e) {
			log(e);
			return new NetworkList();
		}
	}
	
	public Network queryNetwork(String networkId, NetworkQuery nodeSearch) {	
	//public CompletableFuture<AspectList> findNodes(String keywords, String networkId) {	
			System.out.println("123 searching: " + networkId);
		try {
			HttpEntity<SearchString> request = new HttpEntity<>(nodeSearch, headers); // todo: escape special characters
			Network aspects = rest.postForObject(NODE_SEARCH, request, Network.class, networkId);
			//System.out.println("123 has node: " + AspectList.getAspects(NodeAspect.class, aspects).get(0).getNode().getName());
			return aspects; //return CompletableFuture.completedFuture(aspects);
		
		} catch (Exception e) {
			
			log(e);
			return new Network();
		}
	
	}

}
