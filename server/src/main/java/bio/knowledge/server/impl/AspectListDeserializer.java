package bio.knowledge.server.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import bio.knowledge.server.json.Aspect;

/**
 * Used to deserialize {@code data} (aspect list) property of a {@code Network}.
 * NDEx fills that property with a message object if the net is empty.
 * 
 * @author Meera Godden
 *
 */
public class AspectListDeserializer extends JsonDeserializer<Aspect[]> {
	
	private static Logger _logger = LoggerFactory.getLogger(AspectListDeserializer.class);	
		
	/*
	 * (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)
	 */
	@Override
	public Aspect[] deserialize(JsonParser p, DeserializationContext ctxt) 
			throws IOException, JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

		Aspect[] values = null;
		
		try {
			values = mapper.readValue(p, Aspect[].class);

		} catch (NullPointerException npe) {

			_logger.error("AspectListDeserializer.deserialize() ERROR: Null pointer encountered?");

		} catch (IOException ioe) {

			_logger.error("AspectListDeserializer.deserialize() ERROR: IOException encountered? "+ioe.getMessage());

		} finally {

			if(values==null)
				values = new Aspect[] {}; // return an empty Aspect array
		}
		
		return values;
	}

}
