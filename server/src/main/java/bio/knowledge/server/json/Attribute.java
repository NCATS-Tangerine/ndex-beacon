package bio.knowledge.server.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import bio.knowledge.server.impl.StringListDeserializer;

/**
 * Used by the nodeAttributes and edgeAttributes Aspects to represent node/edge properties as key-value pairs.
 * 
 * @author Meera Godden
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Attribute {
	
	private Long id;
	private String name;
	private List<String> value;
	
	@JsonProperty("po")
	public Long getId() {
		return id;
	}

	@JsonProperty("po")
	public void setId(Long id) {
		this.id = id;
	}
	
	@JsonProperty("n")
	public String getName() {
		return name;
	}

	@JsonProperty("n")
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonProperty("v")
	public List<String> getValues() {
		return value;
	}

	@JsonProperty("v")
	@JsonDeserialize(using = StringListDeserializer.class)
	public void setValues(List<String> value) {
		this.value = value;
	}

}
