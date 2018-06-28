package bio.knowledge.server.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkProperty {
	private String predicateString;
	private String value;

	public String getPredicateString() {
		return predicateString;
	}

	public void setPredicateString(String predicateString) {
		this.predicateString = predicateString;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
	
