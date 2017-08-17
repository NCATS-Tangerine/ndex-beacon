package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonProperty;

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
