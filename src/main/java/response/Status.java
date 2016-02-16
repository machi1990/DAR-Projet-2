package response;

import java.util.HashMap;
import java.util.Map;

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
	NOT_IMPLEMENTED;
	
	
	private static Map<Status, Map<String, String>> map;
	
	static {
		map = new HashMap<>();
		Map<String, String> mapped= new HashMap<>();
		mapped.put("status_code","200");
		mapped.put("text", "OK");
		map.put(OK,mapped);
		
		mapped = new HashMap<>();
		mapped.put("status_code", "201");
		mapped.put("text","CREATED");
		map.put(CREATED,mapped);
		

		mapped = new HashMap<>();
		mapped.put("status_code", "501");
		mapped.put("text","NOT IMPLEMENTED");
		map.put(NOT_IMPLEMENTED,mapped);
		
		// TODO the rest
	}
	
	public String toString() {
		if (map.get(this) == null) {
				return NOT_IMPLEMENTED.toString();
		}
		
		return "HTTP/1.1 "+ map.get(this).get("status_code") + " " + map.get(this).get("text") + "\n\r";
	}
}
