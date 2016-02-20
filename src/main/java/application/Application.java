package application;

import request.ContentType;
import request.Request;
import response.Response;
import response.Status;
import server.annotation.GET;
import server.annotation.PATH;

@PATH("/echo")
public class Application {
	@GET
	public Response get(Request request) {
		Response response = Response.response(Status.OK);
		response.setContentType(ContentType.JSON);
		response.build(request);
		return response;
	}
}
