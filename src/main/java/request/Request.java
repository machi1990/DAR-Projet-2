package request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Request {
	private String host;
	private String body;
	private Method method;
	private Headers headers;

	public Request() {
		super();
	}
	
	public Request(String host, String body,Method method, Headers headers) {
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
	
	public void setMethod(String method) {
		this.method = Method.valueOf(method);
	}
	
	public Headers getHeaders() {
		return headers;
	}

	public void setHeaders(Headers headers) {
		this.headers = headers;
	}
	
	@Override
	public String toString() {
		
		ContentType type = this.headers != null ? this.headers.getContentType() : ContentType.PLAIN;
		
		switch (type) {
		case HTML: 
			return htmlify();
		case JSON:
			return jsonify();
		default:
			return  "<br>Host: "+ this.host + 
					"<br>Method: "+ this.method.name() +
					"<br>Body: " + this.body + 
					"<br>Headers: " + this.headers.toString();
		}
	}
	
	private String jsonify() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "{}";
		}
	}
	
	private String htmlify() {
		String html = "<table border=\"1\" style=\"width:100%\">"
				+ "<thead><tr> "
				+ "<th> HOST </th>"
				+ "<th> METHOD </th>"
				+ "<th> HEADERS </th>"
				+ "<th> BODY </th>"
				+ "</tr></thead>"
				+ "<tbody><tr>"
				+ "<td>"+this.host+"</td>"
				+ "<td>"+this.method+"</td>"
				+ "<td>"+this.body+"</td>"
				+ "<td>"+this.headers.toString()+"</td>"
				+ "</tr><tbody>"
				+ "</table>";
		
		return html;
	}
}
