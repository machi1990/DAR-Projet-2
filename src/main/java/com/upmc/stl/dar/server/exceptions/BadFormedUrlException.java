package com.upmc.stl.dar.server.exceptions;

import java.lang.reflect.Method;

public class BadFormedUrlException extends ParamNotAcceptableException {
	private static final long serialVersionUID = -3965698135356518557L;

	protected BadFormedUrlException(String value, Method method, Class<?> clazz) {
		super(value, method, clazz);
	}

	@Override
	public String getMessage() {
		return "Bad formed url value: \"" + value + 	
				(hasMethod() ? "\" inside method + \" " + method.getName() : "") + 
				"\" \nof class \" " + clazz.getName() + " \"."
				+ "This can be due to url containing characters that "
				+ "are not allowed and/or unmatched annotated PARAM."
				+ "Make sure that your url is well formed";
	}
	
	private boolean hasMethod() {
		return method != null;
	}
}
