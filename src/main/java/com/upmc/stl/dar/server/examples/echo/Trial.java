package com.upmc.stl.dar.server.examples.echo;

import com.upmc.stl.dar.server.annotation.GET;
import com.upmc.stl.dar.server.annotation.PATH;
import com.upmc.stl.dar.server.annotation.POST;
import com.upmc.stl.dar.server.annotation.PRODUCES;
import com.upmc.stl.dar.server.request.ContentType;
import com.upmc.stl.dar.server.request.Request;
import com.upmc.stl.dar.server.request.UrlParameters;
import com.upmc.stl.dar.server.response.Response;

@PATH("/echo/trial")
public class Trial extends Application {

	@GET
	@PRODUCES(ContentType.JSON)
	@PATH("/json")
	public Container json(Request request,UrlParameters parameter) {
		return new Container(parameter,request);
	}
	
	@POST
	@PRODUCES(ContentType.JSON)
	@PATH("/json")
	public Container jsonfiy(Request request,String parameter) {
		return new Container(parameter,request);
	}
	
	public static class Container {
		public Object  param;
		public Request request;
		
		public Container(Object param, Request request) {
			super();
			this.param = param;
			this.request = request;
		}
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
