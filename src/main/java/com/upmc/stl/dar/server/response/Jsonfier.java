package com.upmc.stl.dar.server.response;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Jsonfier {
	public static String jsonfy(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			// TODO handle this error
			return "{}";
		}
	}
	
	public static Object toJavaObject(String value, Class<?> clazz) throws JsonParseException, JsonMappingException, IOException {
		return new ObjectMapper().readValue(value,clazz);
	}
	
	/**
	 * TODO enchance parser
	 * @param value
	 * @return
	 * @throws JsonParseException
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private static String parse(String value) throws JsonParseException, IOException {
		JsonParser  parser  = new JsonFactory().createParser(value);
		value = value.trim();
		
		boolean isJsonContainer = value.startsWith("{");
		
		StringBuilder json = new StringBuilder("");
		
		if (isJsonContainer) {
			json.append('{');
		}
		
		while(!parser.isClosed()){
		    JsonToken jsonToken = parser.nextToken();

		    if(JsonToken.FIELD_NAME.equals(jsonToken)){
		        String fieldName = "\""+parser.getCurrentName() + "\"";
		        
		        jsonToken = parser.nextToken();

		        json.append(fieldName + ":" + parse(parser.getValueAsString()));
		    }
		}
		
		if (isJsonContainer) {
			json.append("}");
		}
		
		return json.toString();
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(toJavaObject("mambo", String.class));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}