package com.upmc.stl.dar.server.examples.point;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.upmc.stl.dar.server.annotation.CONSUMES;
import com.upmc.stl.dar.server.annotation.GET;
import com.upmc.stl.dar.server.annotation.PARAM;
import com.upmc.stl.dar.server.annotation.PATH;
import com.upmc.stl.dar.server.annotation.POST;
import com.upmc.stl.dar.server.annotation.PRODUCES;
import com.upmc.stl.dar.server.annotation.CONSUMES.Consumed;
import com.upmc.stl.dar.server.request.ContentType;
import com.upmc.stl.dar.server.response.Response;
import com.upmc.stl.dar.server.response.Status;

@PATH("/point")
public class PointApplication {

	private static Map<Long,Point> points = new HashMap<>();
	
	@GET
	@PRODUCES(ContentType.JSON)
	@PATH("/list")
	public Set<Long> list() {
		return points.keySet();
	}
	
	@GET
	@PRODUCES(ContentType.JSON)
	@PATH("/<id>/x")
	public Double getX(@PARAM("<id>") Long id) {
		return points.get(id).getX();
	}
	
	@GET
	@PRODUCES(ContentType.JSON)
	@PATH("/<id>/y")
	public Double json(@PARAM("<id>") Long id ) {
		return points.get(id).getY();
	}
	
	@GET
	@PRODUCES(ContentType.JSON)
	@PATH("/p/<id>")
	public Point getPoint(@PARAM("<id>") Long id) {
		return points.get(id);
	}
	
	@POST
	@PRODUCES(ContentType.JSON)
	@CONSUMES(Consumed.JSON)
	@PATH("/p/<id>")
	public Response postPoint(Point point) {
		points.put((long) (points.size()+1), point);
		
		Response response = Response.response(Status.OK);
		
		response.build(""+points.size());
		
		return response; 
	}
	
}
