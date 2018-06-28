package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkList {
	
	private NetworkSummary[] networks = new NetworkSummary[0];

	public NetworkSummary[] getNetworks() {
		return networks;
	}

	public void setNetworks(NetworkSummary[] networks) {
		this.networks = networks;
	}
	
}
