package bio.knowledge.server.impl;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import bio.knowledge.server.json.Aspect;

/**
 * Used to deserialize the {@code Attribute} value property into a list of strings.
 * The property can be either a single value or an array of values.
 * 
 * @author Meera Godden
 *
 */
public class StringListDeserializer extends JsonDeserializer<List<String>> {

	@Override
	public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		List<Object> values = mapper.readValue(p, List.class);
		List<String> strings = Util.map(Object::toString, values);
		return strings;
	}

}
