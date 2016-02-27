package com.upmc.stl.dar.server.exceptions;

import java.lang.reflect.Method;

public class ParamConflictException extends ParamNotAcceptableException {
	private static final long serialVersionUID = -2172134996312041481L;
	private String first;
	private String second;
	
	protected ParamConflictException(String value, String first, String second, Method method, Class<?> clazz) {
		super(value,method,clazz);
		this.first = first;
		this.second = second;
	}

	@Override
	public String getMessage() {
		return "Detected confict for param name \"" + value + "\" between argument \"" + 
				first +" \" and \" " + second +"\" inside method + \" " + 
				method.getName() + "\" of class \" " + clazz.getName() + " \"";
	}
}
