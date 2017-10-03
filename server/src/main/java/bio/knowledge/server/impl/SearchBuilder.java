package bio.knowledge.server.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import bio.knowledge.server.json.BasicQuery;
import bio.knowledge.server.json.SearchString;

@Service
public class SearchBuilder {
		
	
	public String id(String nodeId) { return "id:" + nodeId; }

	public String phrase(String phrase) { return "\"" + phrase + "\""; }
		
	public String startsWith(String string) { return string.replace(" ", " AND ") + "*"; }
	
	
	public String edgeCount(int between, int and) {
		return "edgeCount:[" + between + " TO " + and + "]";
	}
	
	public String and(String term1, String term2) { return "(" + term1 + " AND " + term2 + ")"; }

	public String and(List<String> terms) { return Util.insert(this::and, terms, ""); }
	
	public String or(String term1, String term2) { return "(" + term1 + " OR " + term2 + ")"; }
	
	public String or(List<String> terms) { return Util.insert(this::or, terms, ""); }
	
	
	public SearchString networksBy(String search) { return new SearchString(search); }
	
	public BasicQuery nodesBy(String search) { return new BasicQuery(search, 0); }

	public BasicQuery edgesBy(String search) { return new BasicQuery(search, 1); }
		
}
