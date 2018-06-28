package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkSummary {

	private String networkId;
	private NetworkProperty[] properties = new NetworkProperty[0];

	@JsonProperty("externalId")
	public String getNetworkId() {
		return networkId;
	}

	@JsonProperty("externalId")
	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}
	
	public NetworkProperty[] getProperties() {
		return properties;
	}

	public void setProperties(NetworkProperty[] properties) {
		this.properties = properties;
	}
}
