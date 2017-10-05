package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Connects edges to supporting text.
 * 
 * @author Meera Godden
 *
 */
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
