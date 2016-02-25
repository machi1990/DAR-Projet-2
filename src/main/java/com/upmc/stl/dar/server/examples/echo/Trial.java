package com.upmc.stl.dar.server.examples.echo;

import com.upmc.stl.dar.server.annotation.GET;
import com.upmc.stl.dar.server.annotation.PATH;
import com.upmc.stl.dar.server.annotation.PRODUCES;
import com.upmc.stl.dar.server.request.ContentType;
import com.upmc.stl.dar.server.request.Request;
import com.upmc.stl.dar.server.response.Response;

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
