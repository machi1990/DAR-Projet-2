package application;

import request.ContentType;
import request.Request;
import response.Response;
import server.annotation.GET;
import server.annotation.PATH;
import server.annotation.PRODUCES;

@PATH("/echo/trial")
public class Trial extends Application {

	@GET
	@PRODUCES(ContentType.JSON)
	@PATH("/json")
	public Request json(Request request) {
		return request;
	}
	
	@GET
	@PATH("/html")
	public Response html(Request request) {
		return super.html(request);
	}
	
	@GET
	@PATH("/plain")
	public Response plain(Request request) {
		return super.plain(request);
	}
}
