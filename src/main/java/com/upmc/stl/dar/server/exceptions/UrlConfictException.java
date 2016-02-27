package com.upmc.stl.dar.server.exceptions;

import com.upmc.stl.dar.server.resource.configuration.Resource;

public class UrlConfictException extends ServerException {
	private static final long serialVersionUID = -366801941695376846L;
	private Resource first;
	private Resource second;
	
	protected UrlConfictException(Resource first, Resource second) {
		super();
		this.first = first;
		this.second = second;
	}

	@Override
	public String getMessage() {
		return "Url schema confict between \" " + first + " \" and " + second;
	}

}
