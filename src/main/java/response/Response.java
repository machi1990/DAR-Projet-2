package response;

import java.util.Date;

import javafx.util.Pair;
import request.Cookie;
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
		 "Set-" + new Cookie(new Pair<String, String>("LSID", "DQAAAK…Eaem_vYg")) + 
		 "Set-" + new Cookie(new Pair<String, String>("HELLO", "DQAAghjsgfhgsjhdgEaem_vYg")) + 
		 ((this.headers == null) ? "Content-Type: text/html \r\n":this.headers.toString());
		
		return header + "\r\n" +this.body; 
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
