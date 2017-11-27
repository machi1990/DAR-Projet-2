package com.upmc.stl.dar.server.examples.echo;

import com.upmc.stl.dar.server.annotation.GET;
import com.upmc.stl.dar.server.annotation.PATH;
import com.upmc.stl.dar.server.annotation.POST;
import com.upmc.stl.dar.server.annotation.PRODUCES;
import com.upmc.stl.dar.server.request.ContentType;
import com.upmc.stl.dar.server.request.Request;
import com.upmc.stl.dar.server.response.Response;
import com.upmc.stl.dar.server.response.Status;

@PATH("/echo")
public class Application {
	
	@GET
	@PATH("/html")
	public Response html(Request request) {
		Response response = Response.response(Status.OK);
		response.setContentType(ContentType.HTML);
		response.build(htmlify(request));
		return response;
	}
	
	@GET
	@PRODUCES(ContentType.JSON)
	@PATH("/json")
	public Request json(Request request) {
		return request;
	}
	
	@POST
	@PRODUCES(ContentType.JSON)
	@PATH("/json")
	public Request jsonPost(Request request) {
		return request;
	}
	
	@GET
	@PATH("/plain")
	public Response plain(Request request) {
		Response response = Response.response(Status.OK);
		response.setContentType(ContentType.PLAIN);
		response.build(request);
		return response;
	}
	

	private static String htmlify(Request request) {
		return "<table border=\"1\" style=\"width:100%\">"
				+ "<thead><tr> "
				+ "<th> METHOD </th>"
				+ "<th> URL </th>"
				+ "<th> HEADERS </th>"
				+ "<th> COOKIES</th>"
				+ (request.hasBody() ?"<th> BODY </th>":"")
				+ "</tr></thead>"
				+ "<tbody><tr>"
				+ "<td>"+request.getMethod()+"</td>"
				+ "<td>"+request.getUrl()+"</td>"
				+ "<td>"+request.getHeaders()+"</td>"
				+ "<td>"+request.stringfyCookies().replaceAll("\r\n", "<br>")+"</td>"
				+ (request.hasBody() ?"<td>"+request.getBody()+"</td>":"")
				+ "</tr><tbody>"
				+ "</table>";
	}
}
