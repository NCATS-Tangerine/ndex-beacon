package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Base class of nodes (like {@code EdgeCitation} and {@code EdgeSupport}) that identify a list of other nodes.
 * 
 * @author Meera Godden
 *
 */
public class IdentifyingObject {

	private Long[] id;

	@JsonProperty("po")
	public Long[] getId() {
		return id;
	}

	@JsonProperty("po")
	public void setId(Long[] id) {
		this.id = id;
	}
	
}
