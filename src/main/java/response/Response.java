package response;

import request.Headers;
import request.Method;

public class Response {
	private String host;
	private String body;
	private Method method;
	private Headers headers;
	
	public Response() {
		super();
	}
	
	public Response(String host, String body, Method method, Headers headers) {
		super();
		this.host = host;
		this.body = body;
		this.method = method;
		this.headers = headers;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
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
	
	public void setMethod(Method method) {
		this.method = method;
	}
	
	public Headers getHeaders() {
		return headers;
	}
	
	public void setHeaders(Headers headers) {
		this.headers = headers;
	}
	
}
