package com.upmc.stl.dar.server.exceptions;

import java.lang.reflect.Method;

public class ParamConflictException extends Exception {
	private static final long serialVersionUID = -2172134996312041481L;
	private String value;
	private String first;
	private String second;
	private Method method;
	private Class<?> clazz;
	
	public ParamConflictException(String value, String first, String second, Method method, Class<?> clazz) {
		super();
		this.value = value;
		this.first = first;
		this.second = second;
		this.method = method;
		this.clazz = clazz;
	}

	@Override
	public String getMessage() {
		return "Detected confict for param name \"" + value + "\" between argument \"" + 
				first +" \" and \" " + second +"\" inside method + \" " + 
				method.getName() + "\" of class \" " + clazz.getName() + " \"";
	}
}
