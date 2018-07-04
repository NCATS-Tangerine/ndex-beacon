package bio.knowledge.server.json;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * A {@code Network} object contains an array of aspects.
 * Each {@code Aspect} has only one property, the name of which tells you what type of data that property contains.
 * Thus, only one of the fields of this class will be populated at a time.
 * A single {@code Aspect} class was used to avoid the use of a custom deserializer.
 * 
 * @author Meera Godden
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Aspect {
		
	private Node[] nodes = new Node[0];
	private Edge[] edges = new Edge[0];
	private Namespace[] namespaces = new Namespace[0]; 
	private Attribute[] nodeAttributes = new Attribute[0];
	private Attribute[] edgeAttributes = new Attribute[0];
	private Citation[] citations = new Citation[0];
	private EdgeCitation[] edgeCitations = new EdgeCitation[0];
	private Support[] supports = new Support[0];
	private EdgeSupport[] edgeSupports = new EdgeSupport[0];

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

	@JsonProperty("@context")
	public Namespace[] getNamespaces() {
		return namespaces;
	}
	
	@JsonProperty("@context")
	public void setNamespaces(Namespace[] namespaces) {
		this.namespaces = namespaces;
	}
	
}
