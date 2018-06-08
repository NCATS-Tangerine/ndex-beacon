package bio.knowledge.server.json;

public class BasicQuery extends SearchString {
	
	private int searchDepth;
	private int edgeLimit;

	
	public BasicQuery(String searchString, int searchDepth) {
		super(searchString);
		this.searchDepth = searchDepth;
		this.edgeLimit = 0;
	}
	
	public BasicQuery(String searchString, int searchDepth, int edgeLimit) {
		super(searchString);
		this.searchDepth = searchDepth;
		this.edgeLimit = edgeLimit;
	}

	public int getSearchDepth() {
		return searchDepth;
	}
	
	public int getEdgeLimit() {
		return edgeLimit;
	}
}
