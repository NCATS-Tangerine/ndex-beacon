package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Attribute {
	
	private Long id;
	private String name;
	private String value;
	
	@JsonProperty("po")
	public Long getId() {
		return id;
	}

	@JsonProperty("po")
	public void setId(Long id) {
		System.out.println("123 node id: " + id);
		this.id = id;
	}
	
	@JsonProperty("n")
	public String getName() {
		return name;
	}

	@JsonProperty("n")
	public void setName(String name) {
		System.out.println("123 node name: " + name);
		if (name.equals("HGNC")) System.out.println("123 FOUND IT");
		this.name = name;
	}
	
	@JsonProperty("v")
	public String getValue() {
		return value;
	}

	@JsonProperty("v") // todo: refactor with property...
	public void setValue(String value) {
		System.out.println("123 node val: " + value);
		this.value = value;
	}
}
