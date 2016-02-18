package request;

import java.util.HashMap;
import java.util.Map;

public class Headers {
	// private String host;
	// private Method method;
	// TODO add more headers here or we can use a map
	
	private Map<String,Object> headers = new HashMap<>();
	
	
	public void put(String header,String value) {
		if (header.equals("Content-Type")) {
			setContentType(value);
		} else if (header.equals("Accept") && ContentType() == null) {
			setContentType(value);
		}else {
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

	@Override
	public String toString() {
		String headers = "";
		
		for (String header: this.headers.keySet()) {
			headers += header + ": "+ this.headers.get(header).toString() + "\r\n"; 
		}
		
		return headers;
	}
}
