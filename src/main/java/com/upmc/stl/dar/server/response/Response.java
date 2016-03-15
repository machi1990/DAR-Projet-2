package com.upmc.stl.dar.server.response;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.upmc.stl.dar.server.HttpServer;
import com.upmc.stl.dar.server.request.ContentType;
import com.upmc.stl.dar.server.request.Cookie;
import com.upmc.stl.dar.server.request.Headers;
import com.upmc.stl.dar.server.tools.Session;

import javafx.util.Pair;

/**
 * TODO
 */
public class Response {
	private StringBuilder body;
	private Headers headers;
	private Status status;
	private Map<String,Cookie> cookies = new HashMap<>();
	
	public Response() {
		this(Status.OK,new Headers());
	}
	
	public Response(Status status,Headers headers) {
		super();
		this.status = status;
		this.headers = headers;
		this.headers.put("Connection", "keep-alive");
		this.headers.put("Server",HttpServer.SERVER_NAME);
	}
	
	public Response(Status status) {
		this(status,new Headers());
	}
	
	public String getBody() {
		return body.toString();
	}
	
	public void setBody(StringBuilder body) {
		this.body = body;
	}
		
	public Headers getHeaders() {
		return headers;
	}
	
	public void setHeaders(Map<String, Object> headers) {
		this.headers.putAll(headers);
	}
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	public void setContentType(ContentType type) {
		this.headers.put("Content-Type", type.toString());
	}
	
	public void setContentType(String type) {
		this.headers.put("Content-Type", type);
	}
	
	public Map<String, Cookie> getCookies() {
		return Collections.unmodifiableMap(cookies);
	}

	public Cookie getCookie(String key) {
		return cookies.get(key);
	}
	
	public void addCookies(Map<String, Cookie> cookies) {
		this.cookies.putAll(cookies);
	}
	
	public void addCookie(String key, String value) {
		cookies.put(key, new Cookie(new Pair<String, String>(key, value)));
	}

	public void addCookie(Cookie cookie) {
		cookies.put(cookie.cookieKey(), cookie);
	}
	
	public void newSession() {
		Session session = Session.newInstance();
		cookies.put(session.cookieKey(), session);
	}
	
	public void addSession(Session session) {
		if (session == null) {
			return;
		}
		
		cookies.put(session.cookieKey(), session);
	}
	
	public void build(Object object) {
		if (object == null) {
			return;
		}
		
		if (this.body == null) {
			this.body = new StringBuilder();
		} 
		
		this.body.append(object.toString());
	}
	
	public void write() {
		if (this.body == null) {
			this.body = new StringBuilder("");
		} 
		
		String result =  this.toString();	
		System.out.println(result);
		
		// TODO writer.write(result);
	}

	@Override
	public String toString() {
		this.headers.put("Date", new Date().toString());
		
		String header =  status + this.headers.toString() + stringifyCookie();
		
		return header.replaceAll(HttpServer.separtor()+"{2,}", "") + this.body; 
	}
	
	private String stringifyCookie() {
		StringBuilder result = new StringBuilder();
		for (String key:cookies.keySet()) {
			result.append("Set-"+ cookies.get(key)); 
		}
		return result.toString().isEmpty() ? HttpServer.separtor(): result.toString();
	}
	
	public static Response response(Status status) {
		return new Response(status);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		
		Response response = (Response) object;
		if (body == null) {
			if (response.body != null)
				return false;
		} else if (!body.equals(response.body))
			return false;
		if (headers == null) {
			if (response.headers != null)
				return false;
		} else if (!headers.equals(response.headers))
			return false;
		if (status != response.status)
			return false;
		return true;
	}
	
	
}
