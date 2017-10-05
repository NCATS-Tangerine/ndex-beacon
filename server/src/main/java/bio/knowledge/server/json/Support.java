package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Supporting text associated to a citation.
 * 
 * @author Meera Godden
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Support extends IdentifiedObject {

	private String text;
	private Long citationId;
	
	private Citation citation;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@JsonProperty("citation")
	public Long getCitationId() {
		return citationId;
	}

	@JsonProperty("citation")
	public void setCitationId(Long citationId) {
		this.citationId = citationId;
	}
	
	public void setCitation(Citation citation) {
		this.citation = citation;
	}
	
	public Citation getCitation() {
		return citation;
	}
	
}
