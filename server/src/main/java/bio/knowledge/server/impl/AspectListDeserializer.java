package bio.knowledge.server.impl;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import bio.knowledge.server.json.Aspect;

/**
 * Used to deserialize {@code data} (aspect list) propery of a {@code Network}.
 * NDEx fills that property with a message object if the net is empty.
 * 
 * @author Meera Godden
 *
 */
public class AspectListDeserializer extends JsonDeserializer<Aspect[]> {
	
	@Override
	public Aspect[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		Aspect[] values = mapper.readValue(p, Aspect[].class);
		return values;
	}

}
