package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AdvancedQuery {
	
	private NodeFilter nodeFilter = new NodeFilter();

	public NodeFilter getNodeFilter() {
		return nodeFilter;
	}

	public void setNodeFilter(NodeFilter nodeFilter) {
		this.nodeFilter = nodeFilter;
	}
	
	public void addNodeFilter(String name, String value) {
		Property prop = new Property(name, value);
		nodeFilter.addProperty(prop);
	};

}
