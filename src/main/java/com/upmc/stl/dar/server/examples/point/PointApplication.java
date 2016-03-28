package com.upmc.stl.dar.server.examples.point;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.upmc.stl.dar.server.annotation.CONSUMES;
import com.upmc.stl.dar.server.annotation.GET;
import com.upmc.stl.dar.server.annotation.PARAM;
import com.upmc.stl.dar.server.annotation.PATH;
import com.upmc.stl.dar.server.annotation.POST;
import com.upmc.stl.dar.server.annotation.PRODUCES;
import com.upmc.stl.dar.server.annotation.PUT;
import com.upmc.stl.dar.server.annotation.CONSUMES.Consumed;
import com.upmc.stl.dar.server.request.ContentType;
import com.upmc.stl.dar.server.request.UrlParameters;
import com.upmc.stl.dar.server.response.Response;
import com.upmc.stl.dar.server.response.Status;

@PATH("/point")
public class PointApplication {

	private static Map<Long,Point> points = new HashMap<>();
	
	static {
		initializePointsDB();
	}
	
	private static void initializePointsDB() {
		Point point;
		Random random = new Random();
		for (Integer index = 0; index < 10; ++index) {
			point = new Point(random.nextInt(1000), random.nextInt(1000));
			points.put(Long.parseLong(index.toString()), point);
		}
	}
	
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
	public Double json(@PARAM("<id>") Long id) {
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
	@PATH("/post")
	public Response postPoint(Point point) {
		points.put((long) (points.size()+1), point);
		Response response = Response.response(Status.OK);
		response.build(""+points.size());
		return response; 
	}
	
	@POST
	@PRODUCES(ContentType.JSON)
	@CONSUMES(Consumed.JSON)
	@PATH("/post/<first>/<second>")
	public Point test(@PARAM("<second>") Double second,@PARAM("<first>") Double first) {
		points.put((long) (points.size()+1), new Point((int) Math.floor(first), (int) Math.floor(second)));
		return new Point((int) Math.floor(first), (int) Math.floor(second)); 
	}
	
	@PUT
	@PATH("/put/<id>")
	public void putPoint(@PARAM("<id>") Long id, UrlParameters params){
		points.put(id, new Point(Integer.valueOf(params.get("x")), Integer.valueOf(params.get("y"))));
	}
	
}
