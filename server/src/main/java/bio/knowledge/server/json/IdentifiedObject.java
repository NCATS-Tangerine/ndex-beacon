package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Base class of identifiable nodes like {@code Node}, {@code Edge}, and {@code Citation}.
 * 
 * 
 * @author Meera Godden
 *
 */
public class IdentifiedObject {

	private Long id;
	
	@JsonProperty("@id")
	public Long getId() {
		return id;
	}

	@JsonProperty("@id")
	public void setId(Long id) {
		this.id = id;
	}
	
}
