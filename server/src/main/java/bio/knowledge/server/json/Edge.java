package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Edge {
	
	private Long id;
	private String name;
	private Long source;
	private Long target;
	
	private Node subject;
	private Node object;
	
	@JsonProperty("@id")
	public Long getId() {
		return id;
	}
	
	@JsonProperty("@id")
	public void setId(Long id) {
		this.id = id;
	}

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

}
