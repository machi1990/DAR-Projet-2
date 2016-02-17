package request;

import java.util.HashMap;
import java.util.Map;

public class Headers {
	// private String host;
	// private Method method;
	// TODO add more headers here or we can use a map
	
	private Map<String,Object> headers = new HashMap<>();
	
	
	public void put(String header,String value) {
		if (header.equals("Content-Type") || header.equals("Accept")) {
			setContentType(value);
		} else if (header.equals("Cookie")) {
			this.headers.put(header, Cookie.fromString(value));
		} else {
			this.headers.put(header, value);
		}
	}
	
	public ContentType ContentType() {
		return (ContentType) this.headers.get("Content-Type");
	}
	
	public void setContentType(String type) {
		if (type == null) {
			this.headers.put("Content-Type",ContentType.HTML);
		}
		
		if (type.contains("html")) {
			this.headers.put("Content-Type",ContentType.HTML);
		} else if (type.contains("json")) {
			this.headers.put("Content-Type",ContentType.JSON);
		} else {
			this.headers.put("Content-Type",ContentType.PLAIN);
		}
	}
	
	
	public Map<String, Object> getHeaders() {
		return headers;
	}

	/**
	 * TODO Cookie treatment
	 */
	@Override
	public String toString() {
		String headers = "";
		
		for (String header: this.headers.keySet()) {
			if (header.equals("Cookie")) {
				@SuppressWarnings("unchecked")
				Map<String,Cookie> cookies = (Map<String,Cookie>) this.headers.get(header);
				for (String cookie:cookies.keySet()) {
					headers +=cookies.get(cookie);  
				}
				continue;
			} 
			
			headers += header + ": "+ this.headers.get(header).toString() + "\r\n"; 
		}
		
		return headers;
	}
}
