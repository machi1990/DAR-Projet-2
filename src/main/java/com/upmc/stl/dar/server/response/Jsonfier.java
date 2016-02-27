package com.upmc.stl.dar.server.response;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Jsonfier {
	public static String jsonfy(Object object) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		return mapper.writeValueAsString(object);
	}
	
	public static Object toJavaObject(String value, Class<?> clazz) throws JsonParseException, JsonMappingException, IOException {
		return new ObjectMapper().readValue(value,clazz);
	}
	
}