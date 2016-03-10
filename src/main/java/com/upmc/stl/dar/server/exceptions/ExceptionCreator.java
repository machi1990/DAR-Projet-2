package com.upmc.stl.dar.server.exceptions;

import java.lang.reflect.Method;

import com.upmc.stl.dar.server.configuration.resources.Resource;

public class ExceptionCreator {
	private static ExceptionCreator creator = new ExceptionCreator();
	private ServerException exception = new ServerException() {
		private static final long serialVersionUID = 8117370878554756926L;
	};
	
	private ExceptionCreator() {
		super();
	}

	/**
	 * Creates a not matched, a non supported, bad input or resource not found exception.
	 * @param kind
	 * @return
	 */
	public ServerException create(ExceptionKind kind) {
		switch (kind) {
		case NOT_MATCHED:
			return new NotMatchedException();
		case NOT_SUPPORTED:
			return new NotSupportedException();
		case BAD_INPUT:
			return new BadInputException();
		case NOT_FOUND:
			return new ResourceNotFoundException();
		default:
			return exception;
		}
	}
	
	/**
	 * Creates a url-param conflict exception or a bad formed url exception, or a not acceptable param exception.
	 * @param kind
	 * @param value
	 * @param method
	 * @param clazz
	 * @return
	 */
	public ServerException create(ExceptionKind kind,String value, Method method, Class<?> clazz) {
		switch (kind) {
		case URL_PARAM_CONFLICT:
			return new UrlParamConflictException(value, method, clazz);
		case BAD_FORMED_URL:
			return new BadFormedUrlException(value, method, clazz);
		case PARAM_NOT_ACCEPTABLE:
			return new ParamNotAcceptableException(value, method, clazz);
		default:
			return exception;
		}
	}
	
	/**
	 * Creates a url-param conflict exception for a given value between two parameters .
	 * @param value
	 * @param first name of first parameter
	 * @param second name of second parameter
	 * @param method
	 * @param clazz
	 * @return
	 */
	public ServerException create(String value, String first, String second, Method method, Class<?> clazz) {
		return new ParamConflictException(value, first, second, method, clazz);
	}
	
	/**
	 * Creates a url conflict exception between two resources
	 * @param first {@link Resource}
	 * @param second {@link Resource}
	 * @return
	 */
	public ServerException create(Resource first, Resource second) {
		return new UrlConfictException(first, second);
	}
	
	public static enum ExceptionKind {
		BAD_INPUT,
		BAD_FORMED_URL,
		NOT_MATCHED,
		NOT_SUPPORTED,
		PARAM_NOT_ACCEPTABLE,
		NOT_FOUND,
		URL_CONFLICT,
		URL_PARAM_CONFLICT
	}
	
	public synchronized static ExceptionCreator creator() {
		return creator;
	}
}
