package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkList {
	
	private NetworkId[] networks = new NetworkId[0];

	public NetworkId[] getNetworks() {
		return networks;
	}

	public void setNetworks(NetworkId[] networks) {
		this.networks = networks;
	}
	
}
