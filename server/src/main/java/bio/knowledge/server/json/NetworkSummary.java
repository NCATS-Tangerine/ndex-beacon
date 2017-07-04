package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// todo: delete?
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkSummary extends NetworkId {

	private String description;
	private String name;
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
