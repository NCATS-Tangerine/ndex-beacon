package bio.knowledge.server.json;

import java.util.Arrays;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Aspect {
	
	// todo: document shouldn't return null
	
	private NetworkId[] ndexStatus = new NetworkId[0];
	private Node[] nodes = new Node[0];
	private Edge[] edges = new Edge[0];
	
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
	
}
