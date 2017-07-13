package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Aspect {
	
	// todo: document shouldn't return null
	
	private NetworkId[] ndexStatus = new NetworkId[0];
	private Node[] nodes = new Node[0];
	private Edge[] edges = new Edge[0];
	private Attribute[] nodeAttributes = new Attribute[0];
	private Attribute[] edgeAttributes = new Attribute[0];
	private Citation[] citations = new Citation[0];
	private EdgeCitation[] edgeCitations = new EdgeCitation[0];
	private Support[] supports = new Support[0];
	private EdgeSupport[] edgeSupports = new EdgeSupport[0];
	
	public NetworkId[] getNdexStatus() {
		return ndexStatus;
	}

	public void setNdexStatus(NetworkId[] ndexStatus) {
		this.ndexStatus = ndexStatus;
	}

	public Node[] getNodes() {
		return nodes;
	}

	public void setNodes(Node[] nodes) {
		this.nodes = nodes;
	}

	public Edge[] getEdges() {
		return edges;
	}

	public void setEdges(Edge[] edges) {
		this.edges = edges;
	}

	public Attribute[] getNodeAttributes() {
		return nodeAttributes;
	}

	public void setNodeAttributes(Attribute[] nodeAttributes) {
		this.nodeAttributes = nodeAttributes;
	}

	public Attribute[] getEdgeAttributes() {
		return edgeAttributes;
	}

	public void setEdgeAttributes(Attribute[] edgeAttributes) {
		this.edgeAttributes = edgeAttributes;
	}

	public Citation[] getCitations() {
		return citations;
	}

	public void setCitations(Citation[] citations) {
		this.citations = citations;
	}

	public EdgeCitation[] getEdgeCitations() {
		return edgeCitations;
	}

	public void setEdgeCitations(EdgeCitation[] edgeCitations) {
		this.edgeCitations = edgeCitations;
	}

	public Support[] getSupports() {
		return supports;
	}

	public void setSupports(Support[] supports) {
		this.supports = supports;
	}

	public EdgeSupport[] getEdgeSupports() {
		return edgeSupports;
	}

	public void setEdgeSupports(EdgeSupport[] edgeSupports) {
		this.edgeSupports = edgeSupports;
	}
	
}
