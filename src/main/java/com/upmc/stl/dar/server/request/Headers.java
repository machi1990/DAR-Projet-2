package com.upmc.stl.dar.server.request;

import java.util.HashMap;
import java.util.Map;

import com.upmc.stl.dar.server.HttpServer;


public class Headers {
	private Map<String,Object> headers = new HashMap<>();
	
	public Headers() {
		super();
	}

	public Headers(Map<String, Object> headers) {
		super();
		this.headers = headers;
	}
	
	public void put(String header,String value) {
		if (header.equals("Content-Type")) {
			setContentType(value);
		} else {
			this.headers.put(header, value);
		}
	}
	
	public ContentType contentType() {
		return (ContentType) this.headers.get("Content-Type");
	}
	
	public void setContentType(ContentType type) {
		this.headers.put("Content-Type",type);
	}
	
	private void setContentType(String type) {
		if (type == null) {
			this.headers.put("Content-Type",ContentType.HTML);
		}
		
		if (type.contains("/html")) {
			this.headers.put("Content-Type",ContentType.HTML);
		} else if (type.contains("/json")) {
			this.headers.put("Content-Type",ContentType.JSON);
		} else if (type.contains("plain")){
			this.headers.put("Content-Type",ContentType.PLAIN);
		} else {
			this.headers.put("Content-Type",type);
		}
	}
	
	
	public Map<String, Object> getHeaders() {
		return headers;
	}

	@Override
	public String toString() {
		String headers = "";
		
		for (String header: this.headers.keySet()) {
			headers += header + ": "+ this.headers.get(header).toString() + HttpServer.separtor(); 
		}
		
		return headers;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Headers headers_ = (Headers) obj;
		if (headers == null) {
			if (headers_.headers != null)
				return false;
		} else if (!headers.equals(headers_.headers))
			return false;
		return true;
	}

	public void putAll(Map<String, Object> headers) {
		this.headers.putAll(headers);
	}

	public void remove(String key) {
		this.headers.remove(key);
	}
}
