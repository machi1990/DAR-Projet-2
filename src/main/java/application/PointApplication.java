package application;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import request.ContentType;
import request.Request;
import response.Response;
import response.Status;
import server.annotation.CONSUMES;
import server.annotation.CONSUMES.Consumed;
import server.annotation.DELETE;
import server.annotation.GET;
import server.annotation.PARAM;
import server.annotation.PATH;
import server.annotation.POST;
import server.annotation.PRODUCES;
import server.annotation.PUT;

@PATH("/point")
public class PointApplication {

	private static Map<Long,Point> points = new HashMap<>();
	
	@GET
	@PRODUCES(ContentType.JSON)
	@PATH("/list")
	public Set<Long> list(Request request) {
		// just for test get
		points.put((long) 0, new Point(4,2));
		return points.keySet();
	}
	
	@GET
	@PRODUCES(ContentType.JSON)
	@PATH("p/<id>/x")
	public Double getX(@PARAM("<id>") Long id) {
		// just for test get
		points.put((long) 0, new Point(4,2));
		return points.get(id).getX();
	}
	
	@GET
	@PRODUCES(ContentType.JSON)
	@PATH("p/<id>/y")
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
	@PATH("/p")
	public Response postPoint(Point point) {
		points.put((long) (points.size()+1), point);
		
		Response response = Response.response(Status.OK);
		
		response.build(""+points.size());
		
		return response; 
	}
	
	@PUT
	@PATH("/p/<id>")
	public void putPoint(@PARAM("<id>") Long id, Double x, Double y) {
		Point point = new Point();
		point.setLocation(x, y);
		points.put(id, point);
		
	}
	
	@DELETE
	@PATH("/p/<id>")
	public void deletePoint(@PARAM("<id>") Long id) {
		points.remove(id);
	}
}
