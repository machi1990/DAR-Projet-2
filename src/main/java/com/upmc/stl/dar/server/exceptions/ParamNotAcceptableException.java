package com.upmc.stl.dar.server.exceptions;

import java.lang.reflect.Method;

public class ParamNotAcceptableException extends ServerException {
	private static final long serialVersionUID = -647081796682075741L;
	protected String value;
	protected Method method;
	protected Class<?> clazz;
	
	public ParamNotAcceptableException(String value, Method method, Class<?> clazz) {
		super();
		this.value = value;
		this.method = method;
		this.clazz = clazz;
	}

	@Override
	public String getMessage() {
		return "Unacceptable param value \"" + value + "\" inside method + \" " + 
				method.getName() + "\" of class \" " + clazz.getName() + " \"";
	}
	
}
