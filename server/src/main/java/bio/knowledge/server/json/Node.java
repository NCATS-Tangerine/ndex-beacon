package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Node extends IdentifiedObject {

	private String name;
	private String represents;
	
	private String networkId;

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
		this.represents = represents;
	}

	public String getNetworkId() {
		return networkId;
	}

	public void setNetworkId(String networkId) {
		System.out.println("123 fullnode: " + networkId + ":" + getId());
		this.networkId = networkId;
	}
		
}
