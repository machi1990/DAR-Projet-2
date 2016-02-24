package application;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import request.ContentType;
import response.Response;
import response.Status;
import server.annotation.CONSUMES;
import server.annotation.CONSUMES.Consumed;
import server.annotation.GET;
import server.annotation.PARAM;
import server.annotation.PATH;
import server.annotation.POST;
import server.annotation.PRODUCES;

@PATH("/point")
public class PointApplication {

	private static Map<Long,Point> points = new HashMap<>();
	
	@GET
	@PRODUCES(ContentType.JSON)
	@PATH("/list")
	public Set<Long> list() {
		// just for test get
		points.put((long) 0, new Point(4,2));
		return points.keySet();
	}
	
	@GET
	@PRODUCES(ContentType.JSON)
	@PATH("/<id>/x")
	public Double getX(@PARAM("<id>") Long id) {
		// just for test get
		points.put((long) 0, new Point(4,2));
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
		// just for test get
		points.put((long) 0, new Point(4,2));
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
