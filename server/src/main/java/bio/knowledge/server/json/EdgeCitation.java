package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Associates edges to citations.
 * 
 * @author Meera Godden
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EdgeCitation extends IdentifyingObject {

	private Long[] citations;

	public Long[] getCitations() {
		return citations;
	}

	public void setCitations(Long[] citations) {
		this.citations = citations;
	}
	
}
