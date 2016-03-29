package com.upmc.stl.dar.server.request;

import java.util.HashMap;
import java.util.Map;

import com.upmc.stl.dar.server.tools.Session;

public class Request {
	private String url;
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
	
	public boolean hasActiveSession() {
		return cookies.containsKey(Session.sessionKey);
	}
	
	public Session sessionInstance() {
		return Session.newInstance(cookies.get(Session.sessionKey));
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
	
	public Boolean containsHeader(String key) {
		return headers.getHeaders().containsKey(key.toLowerCase());
	}
	
	public String getHeader(String key) {
		if (containsHeader(key)) {
			return headers.getHeaders().get(key.toLowerCase()).toString();
		}
		
		return null;
	}
	
	public String getUrlParams() {
		Integer index = url.indexOf("?");
		String urlParams = index != -1? url.substring(index + 1): "";
		return urlParams;
	}

	public String getUrl() {
		Integer index = url.indexOf("?");
		String url = index != -1 ? this.url.substring(0, index) : this.url;
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public boolean matchesStaticResource() {
		return getUrl().indexOf('.') != -1;
	}
	
	@Override
	public String toString() {
		return  "Method: "+ this.method.name() + " "+
				"Url: "+ this.url +
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
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null)
			return false;
		if (getClass() != object.getClass())
			return false;
		Request request = (Request) object;
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
		if (url == null) {
			if (request.url != null)
				return false;
		} else if (!url.equals(request.url))
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
	
	public static boolean isForWelcomeFile(Request request) {
		if (request == null) {
			return false;
		}
		
		String url = request.getUrl();
		return url.isEmpty() || url.matches("/");
	}

	public void clearSession() {
		if (!hasActiveSession()) {
			return;
		}
		
		
		this.cookies.remove(Session.sessionKey);
	}
}
