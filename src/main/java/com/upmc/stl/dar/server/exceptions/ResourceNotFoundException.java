package com.upmc.stl.dar.server.exceptions;

public class ResourceNotFoundException extends ServerException {
	private static final long serialVersionUID = -2553765111548824467L;

	public ResourceNotFoundException() {
		super();
	}

	@Override
	public String getMessage() {
		return "No resources found inside the supplied packages/classes";
	}
}
