package com.upmc.stl.dar.server.resource.configuration;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import com.upmc.stl.dar.server.request.Request;
import com.upmc.stl.dar.server.response.Response;
import com.upmc.stl.dar.server.response.Status;

public class Dispatcher {
	private static Dispatcher dispatcher = new Dispatcher();
	private Set<Resource> resources = new HashSet<>();
	
	private Dispatcher() {
		super();
	}
	
	public Object dispatch(Request request) {		
		Object result = null;
		
		boolean matched = false;
		
		for (Resource resource: resources) {
			try {
				result = resource.invoke(request);
				matched = true;
				break;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException | IOException e) {
				Response response = Response.response(Status.INTERNAL_SERVER_ERROR);
				response.build(e.getMessage());
				return response;
			} catch (NotMatchedException e) {
				// Catch and continue
			}
		}
		
		if (!matched) {
			Response response = Response.response(Status.NOT_IMPLEMENTED);
			return response;
		}
		
		return result;
	}

	public void setResources(Set<Resource> resources) {
		this.resources = resources;
	}
		
	public static Dispatcher dispatcher() {
		return dispatcher;
	}
}
