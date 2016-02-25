package com.upmc.stl.dar.server.response;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum Status {
	OK,
	CREATED,
	ACCEPTED,
	NO_CONTENT,
	FOUND,
	BAD_REQUEST,
	FORBIDDEN,
	NOT_FOUND,
	INTERNAL_SERVER_ERROR,
	NOT_IMPLEMENTED, 
	UNAUTHORIZED;
	
	
	private static final Map<Status, Map<String, String>> map;
	
	static {
		map = new ConcurrentHashMap<>();
		Map<String, String> mapped= new HashMap<>();
		mapped.put("status_code","200");
		mapped.put("text", "OK");
		map.put(OK,mapped);
		
		mapped = new HashMap<>();
		mapped.put("status_code", "201");
		mapped.put("text","Created");
		map.put(CREATED,mapped);
		
		mapped = new HashMap<>();
		mapped.put("status_code", "202");
		mapped.put("text","Accepted");
		map.put(ACCEPTED,mapped);
		
		mapped = new HashMap<>();
		mapped.put("status_code", "204");
		mapped.put("text","No Content");
		map.put(NO_CONTENT,mapped);

		mapped = new HashMap<>();
		mapped.put("status_code", "302");
		mapped.put("text","Found");
		map.put(FOUND,mapped);
		
		mapped = new HashMap<>();
		mapped.put("status_code", "400");
		mapped.put("text","Bad Request");
		map.put(BAD_REQUEST,mapped);
		

		mapped = new HashMap<>();
		mapped.put("status_code", "401");
		mapped.put("text","Unauthorized");
		map.put(UNAUTHORIZED,mapped);
		

		mapped = new HashMap<>();
		mapped.put("status_code", "403");
		mapped.put("text","Forbidden");
		map.put(FORBIDDEN,mapped);
		

		mapped = new HashMap<>();
		mapped.put("status_code", "404");
		mapped.put("text","Not Found");
		map.put(NOT_FOUND,mapped);
		
		mapped = new HashMap<>();
		mapped.put("status_code", "501");
		mapped.put("text","Not Implemented");
		map.put(NOT_IMPLEMENTED,mapped);
		

		mapped = new HashMap<>();
		mapped.put("status_code", "500");
		mapped.put("text","Internal Server Error");
		map.put(INTERNAL_SERVER_ERROR,mapped);
		
		// TODO the rest
	}
	
	public String toString() {
		if (map.get(this) == null) {
				return NOT_IMPLEMENTED.toString();
		}
		
		return "HTTP/1.1 "+ map.get(this).get("status_code") + " " + map.get(this).get("text") + "\n\r";
	}
}
