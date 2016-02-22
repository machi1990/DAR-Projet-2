package application;

import request.ContentType;
import request.Request;
import response.Response;
import response.Status;
import server.annotation.GET;
import server.annotation.PATH;
import server.annotation.PRODUCES;

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
	
	@GET
	@PATH("/plain")
	public Response plain(Request request) {
		Response response = Response.response(Status.OK);
		response.setContentType(ContentType.PLAIN);
		response.build(request);
		return response;
	}
	

	private static String htmlify(Request request) {
		String html = "<table border=\"1\" style=\"width:100%\">"
				+ "<thead><tr> "
				+ "<th> METHOD </th>"
				+ "<th> URL </th>"
				+ "<th> HEADERS </th>"
				+ "<th> COOKIES</th>"
				+ (request.hasBody() ?"<th> BODY </th>":"")
				+ "</tr></thead>"
				+ "<tbody><tr>"
				+ "<td>"+request.getMethod()+"</td>"
				+ "<td>"+request.getResourceUrl()+"</td>"
				+ "<td>"+request.getHeaders()+"</td>"
				+ "<td>"+request.stringfyCookies().replaceAll("\r\n", "<br>")+"</td>"
				+ (request.hasBody() ?"<td>"+request.getBody()+"</td>":"")
				+ "</tr><tbody>"
				+ "</table>";
		
		return html;
	}
}
