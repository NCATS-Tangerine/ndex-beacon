package bio.knowledge.server.json;

public class BasicQuery extends SearchString {
	
	private int searchDepth;

	public BasicQuery(String searchString, int searchDepth) {
		super(searchString);
		this.searchDepth = searchDepth;
	}

	public int getSearchDepth() {
		return searchDepth;
	}
}
