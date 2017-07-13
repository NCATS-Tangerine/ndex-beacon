package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
