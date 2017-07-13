package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdentifyingObject {

	private Long[] id;

	@JsonProperty("po")
	public Long[] getId() {
		return id;
	}

	@JsonProperty("po")
	public void setId(Long[] id) {
		System.out.println("123 node id: " + id);
		this.id = id;
	}
	
}
