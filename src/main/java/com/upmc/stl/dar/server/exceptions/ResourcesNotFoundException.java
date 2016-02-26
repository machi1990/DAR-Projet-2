package com.upmc.stl.dar.server.exceptions;

public class ResourcesNotFoundException extends Exception {
	private static final long serialVersionUID = -2553765111548824467L;

	public ResourcesNotFoundException() {
		super();
	}

	@Override
	public String getMessage() {
		return "No resources found inside the supplied packages";
	}
}
