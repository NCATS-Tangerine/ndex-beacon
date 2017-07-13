package bio.knowledge.server.transl;

import org.springframework.stereotype.Service;

import bio.knowledge.server.json.BasicQuery;
import bio.knowledge.server.json.SearchString;

@Service
public class SearchBuilder {
		
	public String id(String nodeId) { return "id:" + nodeId; }

	public String phrase(String phrase) { return "\"" + phrase + "\""; }
		
	public String startsWith(String string) { return string.replace(" ", " AND ") + "*"; }
	
	public SearchString networksBy(String search) { return new SearchString(search); }
	
	public BasicQuery nodesBy(String search) { return new BasicQuery(search, 0); }

	public BasicQuery edgesBy(String search) { return new BasicQuery(search, 1); }

		
}
