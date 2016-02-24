package server.configuration;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import request.Request;
import response.Response;
import response.Status;

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
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
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
