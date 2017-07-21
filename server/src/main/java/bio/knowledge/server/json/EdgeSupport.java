package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EdgeSupport extends IdentifyingObject {

	private Long[] supports;

	public Long[] getSupports() {
		return supports;
	}

	public void setSupports(Long[] supports) {
		this.supports = supports;
	}
	
}
