package request;

public class Request {
	private String url;
	private String body;
	private Method method;
	private Headers headers;

	public Request() {
		super();
	}
	
	public Request(String url, String body,Method method, Headers headers) {
		super();
		this.url = url;
		this.body = body;
		this.method = method;
		this.headers = headers;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
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
