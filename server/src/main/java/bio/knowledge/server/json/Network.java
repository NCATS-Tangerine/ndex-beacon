package bio.knowledge.server.json;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import bio.knowledge.server.impl.Util;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Network {
	
	private Aspect[] data = new Aspect[0];

	public Aspect[] getData() {
		return data;
	}

	public void setData(Aspect[] data) {
		this.data = data;
	}
	
}
