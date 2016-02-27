package com.upmc.stl.dar.server.exceptions;

public class BadInputException extends ServerException {
	private static final long serialVersionUID = 1L;

	protected BadInputException() {
		super();
	}
	
	@Override
	public String getMessage() {
		return "Unexpected end of input.";
	}
}
