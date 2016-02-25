package com.upmc.stl.dar.server.request;

import java.util.HashMap;
import java.util.Map;

public class Request {
	private String resourceUrl;
	private String body;
	private Method method;
	private Headers headers;
	private Map<String, Cookie> cookies = new HashMap<>();
	
	public Request() {
		super();
	}
	
	public Request(String body,Method method, Headers headers) {
		super();
		this.body = body;
		this.method = method;
		this.headers = headers;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public Method getMethod() {
		return method;
	}
	
	public Map<String, Cookie> getCookies() {
		return cookies;
	}

	public Cookie getCookie (String key) {
		return cookies.get(key);
	}
	
	public void setCookies(Map<String, Cookie> cookies) {
		this.cookies = cookies;
	}
	
	public void setCookies(String cookies) {
		this.cookies = Cookie.fromString(cookies); 
	}
	
	public void setMethod(Method method) {
		this.method = method;
	}
	
	public void setMethod(String method) {
		this.method = Method.valueOf(method);
	}
	
	public Headers getHeaders() {
		return headers;
	}

	public void setHeaders(Headers headers) {
		this.headers = headers;
	}
	

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}
	
	@Override
	public String toString() {
		return  "Method: "+ this.method.name() + " "+
				"Url: "+ this.resourceUrl +
				(hasBody() ? "Body: " + this.body:" ") + 
				"Cookies: "+this.stringfyCookies()+
				"Headers: " + this.headers.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((cookies == null) ? 0 : cookies.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((resourceUrl == null) ? 0 : resourceUrl.hashCode());
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
		Request request = (Request) obj;
		if (body == null) {
			if (request.body != null)
				return false;
		} else if (!body.equals(request.body))
			return false;
		if (cookies == null) {
			if (request.cookies != null)
				return false;
		} else if (!cookies.equals(request.cookies))
			return false;
		if (headers == null) {
			if (request.headers != null)
				return false;
		} else if (!headers.equals(request.headers))
			return false;
		if (method != request.method)
			return false;
		if (resourceUrl == null) {
			if (request.resourceUrl != null)
				return false;
		} else if (!resourceUrl.equals(request.resourceUrl))
			return false;
		return true;
	}

	public boolean hasBody() {
		return this.body != null;
	}
	
	public String stringfyCookies() {
		String result = "";
		
		for (String key: cookies.keySet()) {
			result += cookies.get(key);
		}
		
		return result;
	}
}
