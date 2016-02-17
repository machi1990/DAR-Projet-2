package request;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Request {
	private String body;
	private Method method;
	private Headers headers;

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
		
		ContentType type = this.headers != null ? this.headers.ContentType() : ContentType.PLAIN;
		
		switch (type) {
		case HTML: 
			return htmlify();
		case JSON:
			return jsonify();
		default:
			return  "<br>Method: "+ this.method.name() +
					"<br>Body: " + this.body + 
					"<br>Headers: " + this.headers.toString();
		}
	}
	
	private String jsonify() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		
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
				+ "<th> METHOD </th>"
				+ "<th> HEADERS </th>"
				+ "<th> BODY </th>"
				+ "</tr></thead>"
				+ "<tbody><tr>"
				+ "<td>"+this.method+"</td>"
				+ "<td>"+this.body+"</td>"
				+ "<td>"+this.headers.toString()+"</td>"
				+ "</tr><tbody>"
				+ "</table>";
		
		return html;
	}
}
