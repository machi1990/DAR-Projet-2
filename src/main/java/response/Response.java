package response;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javafx.util.Pair;
import request.ContentType;
import request.Cookie;
import request.Headers;
import server.HttpServer;

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
	
	public void setHeaders(Headers headers) {
		this.headers = headers;
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
	
	
	public Map<String, Cookie> getCookies() {
		return Collections.unmodifiableMap(cookies);
	}

	public Cookie getCookie(String key) {
		return cookies.get(key);
	}
	
	public void setCookies(Map<String, Cookie> cookies) {
		this.cookies = cookies;
	}
	
	public void setCookie(String key, String value) {
		cookies.put(key, new Cookie(new Pair<String, String>(key, value)));
	}

	public void build(Object object) {
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
		long now = new Date().getTime();
		
		String header =  status +"Date: " + new Date(now)+ "\r\n" +
		"Server: " + HttpServer.ServerName + 
		stringifyCookie() + this.headers;
		
		return header + "\r\n" +this.body; 
	}
	
	private String stringifyCookie() {
		StringBuilder result = new StringBuilder();
		for (String key:cookies.keySet()) {
			result.append("Set-"+ cookies.get(key)+"\r\n"); 
		}
		
		return result.toString();
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
