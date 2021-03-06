package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import bio.knowledge.server.impl.AspectListDeserializer;

/**
 * Represents a subnet of an NDEx network.
 * 
 * @author Meera Godden
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Network extends NetworkSummary {
	
	private Aspect[] data = new Aspect[0];

	public Aspect[] getData() {
		return data;
	}

	@JsonDeserialize(using = AspectListDeserializer.class)
	public void setData(Aspect[] data) {
		this.data = data;
	}
}
