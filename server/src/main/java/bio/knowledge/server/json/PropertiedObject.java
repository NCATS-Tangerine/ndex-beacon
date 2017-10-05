package bio.knowledge.server.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import bio.knowledge.server.impl.Util;

/**
 * Base class of nodes (like {@code Node} and {@Edge} that can be referred to and have properties).
 * 
 * @author Meera Godden
 *
 */
public class PropertiedObject extends IdentifiedObject {

	private Map<String, Attribute> attributes = new HashMap<>();
	
	public Collection<Attribute> getAttributes() {
		return attributes.values();
	}
	
	public boolean has(String name) {
		return attributes.containsKey(name);
	}
	
	public List<String> get(String name) {
		Attribute attribute = attributes.get(name);
		if (attribute == null) return new ArrayList<>();
		return attribute.getValues();
	}
	
	public List<String> getByRegex(String regex) {	
		Predicate<String> hasMatchingName = n -> n.matches(regex);		
		List<String> names = Util.filter(hasMatchingName, attributes.keySet());
		List<String> values = Util.flatmap(this::get, names);
		return values;
	}
	
	public void addAttribute(Attribute a) {
		
		String name = a.getName();
		
		if (attributes.containsKey(name)) {
			
			List<String> values = attributes.get(name).getValues();
			Set<String> additions = new HashSet<>(a.getValues());
			additions.removeAll(values);
			values.addAll(additions);
		
		} else {
			attributes.put(name, a);
		}
	}
	
	
	
}
