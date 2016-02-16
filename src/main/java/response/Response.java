package response;

import java.util.Date;

import request.Headers;
import server.HttpServerRequest;

public class Response {
	private StringBuilder body;
	private Headers headers;
	private Status status;
	
	public Response() {
		super();
	}
	
	public Response(Status status,Headers headers) {
		super();
		this.status = status;
		this.headers = headers;
	}
	
	public Response(Status status) {
		this(status,null);
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
		long now = new Date().getTime(),expires = now + 100000000 + (long)(Math.random()*1000)	;
		
		String header =  status +"Date: " + new Date(now).toString() + "\r\n" +
		 "Server: " + HttpServerRequest.ServerName + 
		 "Expires: " + new Date(expires).toString() +"\r\n"+
		 ((this.headers == null) ? "Content-Type: text/html \r\n":this.headers.toString());
		
		return header + "\r\n" +this.body; 
	}
	
	public static Response response(Status status) {
		return new Response(status);
	}
	
}
