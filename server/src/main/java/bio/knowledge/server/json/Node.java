package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Node {

	private Long id;
	private String name;
	
	private String networkId;

	@JsonProperty("@id")
	public Long getId() {
		return id;
	}

	@JsonProperty("@id")
	public void setId(Long id) {
		System.out.println("123 node: " + id);
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

	public String getNetworkId() {
		return networkId;
	}

	public void setNetworkId(String networkId) {
		System.out.println("123 fullnode: " + networkId + ":" + id);
		this.networkId = networkId;
	}
		
}
