package com.upmc.stl.dar.server.exceptions;

public class NotSupportedException extends Exception {
	private static final long serialVersionUID = 7015933353596207604L;

	public NotSupportedException() {
		super();
	}

	@Override
	public String getMessage() {
		return "Parameter\'s type is not supported. Only numeric types,enums and strings are supported";
	}
	
}
