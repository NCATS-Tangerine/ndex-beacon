package bio.knowledge.server.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertiedObject extends IdentifiedObject {

	private Map<String, Attribute> attributes = new HashMap<>();
	
	public Collection<Attribute> getAttributes() {
		return attributes.values();
	}
	
	public List<String> get(String name) {
		
		Attribute attribute = attributes.get(name);
		if (attribute == null)
			return new ArrayList<>();
		return attribute.getValues();
	}
	
	public void addAttribute(Attribute a) {
		attributes.put(a.getName(), a);
	}
	
	
	
}
