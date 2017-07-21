package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import bio.knowledge.server.impl.AspectListDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Network {
	
	private Aspect[] data = new Aspect[0];

	public Aspect[] getData() {
		return data;
	}

	@JsonDeserialize(using = AspectListDeserializer.class)
	public void setData(Aspect[] data) {
		this.data = data;
	}
	
}
