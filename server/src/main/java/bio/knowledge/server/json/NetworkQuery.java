package bio.knowledge.server.json;

public class NetworkQuery extends SearchString {
	
	private int searchDepth;

	public NetworkQuery(String searchString, int searchDepth) {
		super(searchString);
		this.searchDepth = searchDepth;
	}

	public int getSearchDepth() {
		return searchDepth;
	}
}
