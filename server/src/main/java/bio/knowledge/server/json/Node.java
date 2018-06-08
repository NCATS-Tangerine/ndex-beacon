package bio.knowledge.server.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import bio.knowledge.server.impl.Util;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Node extends PropertiedObject {

	private String name;
	private String represents;
	
	private String networkId;
	private List<String> synonyms = new ArrayList<>();
	private List<Edge> edges = new ArrayList<>();
	
	public static boolean isCurie(String string) {
		if (string != null) {
			return string.split(":").length > 1;
		} else {
			return false;
		}
	}

	private void makeRepresentsCurie() {
		List<String> curieAliases = Util.filter(Node::isCurie, get("alias"));		
		if (!isCurie(represents) && !curieAliases.isEmpty())
			setRepresents(curieAliases.get(0));
	}
	
	@JsonProperty("n")
	public String getName() {
		return name;
	}

	@JsonProperty("n")
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonProperty("r")
	public String getRepresents() {
		return represents;
	}

	@JsonProperty("r")
	public void setRepresents(String represents) {
		if (isCurie(represents))
			this.represents = represents;
	}

	public String getNetworkId() {
		return networkId;
	}

	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}
	
	public List<String> getSynonyms() {
		return synonyms;
	}

	public void addSynonym(String synonym) {
		synonyms.add(synonym);
	}
	
	@Override
	public void addAttribute(Attribute a) {
		super.addAttribute(a);
		makeRepresentsCurie();
	}
	
	public List<Edge> getEdges() {
		return edges;
	}

	public void addEdge(Edge edge) {
		edges.add(edge);
	}
		
}
