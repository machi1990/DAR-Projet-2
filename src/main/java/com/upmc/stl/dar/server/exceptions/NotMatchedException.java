package com.upmc.stl.dar.server.exceptions;

public class NotMatchedException extends ServerException {
	private static final long serialVersionUID = 7294454445986653792L;

	protected NotMatchedException() {
		super();
	}

	@Override
	public String getMessage() {
		return "Not matched";
	}
}
