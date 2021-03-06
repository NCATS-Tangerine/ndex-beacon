package bio.knowledge.server.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Used to deserialize the {@code Attribute} value property into a list of strings.
 * The property can be either a single value or an array of values.
 * 
 * @author Meera Godden
 *
 */
public class StringListDeserializer extends JsonDeserializer<List<String>> {
	
	private static Logger _logger = LoggerFactory.getLogger(StringListDeserializer.class);	
	
	@Override
	public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

		List<String> strings = null;
		try {
			
			_logger.debug("StringListDeserializer.deserialize(): just BEFORE readValue, with parser: "+p.toString());
			
			@SuppressWarnings("unchecked")
			List<Object> values = mapper.readValue(p, List.class);
			
			_logger.debug("StringListDeserializer.deserialize(): just AFTER readValue");

			if(values!=null && values.size()>0)
				strings = Util.map(Object::toString, values);
			
			_logger.debug("StringListDeserializer.deserialize(): just AFTER mapping values to List<String>");
			
		} catch (NullPointerException npe) {
			
			_logger.error("StringListDeserializer.deserialize() ERROR: Null pointer encountered?");
			
		} catch (IOException ioe) {
			
			_logger.error("StringListDeserializer.deserialize() ERROR: IOException encountered? "+ioe.getMessage());
			
		} finally {
			
			if(strings==null)
				strings = new ArrayList<String>(); // return an empty list
		}
		
		return strings;
	}

}
