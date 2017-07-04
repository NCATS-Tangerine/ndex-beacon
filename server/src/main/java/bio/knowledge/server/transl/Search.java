package bio.knowledge.server.transl;

import bio.knowledge.server.json.NetworkQuery;
import bio.knowledge.server.json.SearchString;

public class Search {
	
	// todo: escape lucene

	// todo: wildcard, whole word
	// todo: rename for purpose (finding networks by keyword)
	public static SearchString networksMatchingAny(String keywords) {
		return new SearchString(keywords);
	}
	
	public static NetworkQuery nodesById(String nodeId) {
		String nodeIdSearch = "id:" + nodeId;
		return new NetworkQuery(nodeIdSearch, 0);
	}
	
	public static NetworkQuery edgesByNodeId(String nodeId) {
		String nodeIdSearch = "id:" + nodeId;
		return new NetworkQuery(nodeIdSearch, 1);
	}
	
	// todo: wildcard, whole word
	// todo: rename for purpose (finding nodes by keyword)
	public static NetworkQuery nodesMatchingAny(String keywords) {
		return new NetworkQuery(keywords, 0);
	}
	
	// todo: wildcard, whole word
	// todo: rename for purpose (finding nodes by keyword)
	public static NetworkQuery edgesMatchingAny(String keywords) {
		return new NetworkQuery(keywords, 1);
	}
	
}
