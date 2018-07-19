package bio.knowledge.server.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Edge extends PropertiedObject {
	
	private String name;
	private Long source;
	private Long target;
	
	private Node subject;
	private Node object;
	private List<Citation> citations = new ArrayList<>();
	private List<Support> supports = new ArrayList<>();
	private NetworkProperty networkReference;

	@JsonProperty("i")
	public String getName() {
		return name;
	}

	@JsonProperty("i")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("s")
	public Long getSource() {
		return source;
	}

	@JsonProperty("s")
	public void setSource(Long source) {
		this.source = source;
	}

	@JsonProperty("t")
	public Long getTarget() {
		return target;
	}

	@JsonProperty("t")
	public void setTarget(Long target) {
		this.target = target;
	}

	public Node getSubject() {
		return subject;
	}

	public void setSubject(Node subject) {
		this.subject = subject;
	}

	public Node getObject() {
		return object;
	}

	public void setObject(Node object) {
		this.object = object;
	}
	
	public List<Citation> getCitations() {
		return citations;
	}
	
	public void setCitations(List<Citation> citations) {
		this.citations = citations;
	}
	
	public List<Support> getSupports() {
		return supports;
	}
	
	public void setSupports(List<Support> support) {
		this.supports = support;
	}

	public NetworkProperty getNetworkReference() {
		return networkReference;
	}

	public void setNetworkReference(NetworkProperty networkReference) {
		this.networkReference = networkReference;
	}

}
