package bio.knowledge.server.json;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeFilter {
	
	private Property[] propertySpecifications = new Property[0];
	private String mode;
	
	public Property[] getPropertySpecifications() {
		return propertySpecifications;
	}
	
	public void setPropertySpecifications(Property[] propertySpecifications) {
		this.propertySpecifications = propertySpecifications;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public void addProperty(Property p) {
		List<Property> next = Arrays.asList(propertySpecifications);
		next.add(p);
		propertySpecifications = next.toArray(propertySpecifications);
	}

}
