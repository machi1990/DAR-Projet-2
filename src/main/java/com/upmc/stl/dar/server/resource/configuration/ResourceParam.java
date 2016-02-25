package com.upmc.stl.dar.server.resource.configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javafx.util.Pair;

import com.google.common.primitives.Primitives;
import com.upmc.stl.dar.server.annotation.PARAM;

public class ResourceParam {
	private Parameter parameter;
	private Integer rankInMethod;
	private Integer rankInUrl;
	
	private Object value;
	
	public ResourceParam(Parameter parameter, Integer rank) throws NotSupportedException {
		super();
		setParameter(parameter);

	}

	public ResourceParam() {
		super();
	}
	
	public String getAnnotationValue() {
		return hasAnnotation() ? parameter.getAnnotation(PARAM.class).value():null;
	}

	public boolean hasAnnotation() {
		return parameter.getAnnotation(PARAM.class) != null;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(String value) throws IllegalAccessException, IllegalArgumentException {
		this.value = valueOf(value);
	}
	
	public Integer getRankInMethod() {
		return rankInMethod;
	}

	public void setRankInMethod(Integer rankInMethod) {
		this.rankInMethod = rankInMethod;
	}

	public void setParameter(Parameter parameter) throws NotSupportedException {
		if (!hasSupportedType(parameter)) {
			throw new NotSupportedException();
		}
		
		this.parameter = parameter;
	}


	private Object valueOf(String value) throws IllegalAccessException, IllegalArgumentException {
		
		Class<?> clazz = Primitives.wrap(parameter.getType());
		
		if ( clazz == String.class) {
			return value;
		}
		
		try {
			Method  method = clazz.getMethod("valueOf",String.class);
			method.setAccessible(true);
			return method.invoke(null, value); // valueOf is static
			
		} catch (NoSuchMethodException | SecurityException | InvocationTargetException e) {
			
		}
		return null;
	}
	
	public boolean isDigitType() {
		Class<?> clazz = Primitives.wrap(parameter.getType());
		return clazz == Long.class || clazz == Integer.class || clazz == Short.class || clazz == Byte.class;
	}
	
	public boolean isNumericType() {
		return Number.class.isAssignableFrom(Primitives.wrap(parameter.getType()));
	}
	
	public Pair<String,String> toPattern() {
		if (!hasAnnotation()) {
			return null;
		}
		
		String pattern = "\\w+";
		
		if (isDigitType()) {
			pattern = "\\d{1,20}";
		} else if (isNumericType()) {
			pattern = "^\\d+(\\.\\d{1,20})?$";
		}
		
		return new Pair<String, String>(getAnnotationValue(), pattern);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parameter == null) ? 0 : parameter.hashCode());
		result = prime * result + ((rankInMethod == null) ? 0 : rankInMethod.hashCode());
		result = prime * result + ((rankInUrl == null) ? 0 : rankInUrl.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		ResourceParam resourceParam = (ResourceParam) obj;
		
		if (parameter == null) {
			if (resourceParam.parameter != null)
				return false;
		} else if (!parameter.equals(resourceParam.parameter))
			return false;
		if (rankInMethod == null) {
			if (resourceParam.rankInMethod != null)
				return false;
		} else if (!rankInMethod.equals(resourceParam.rankInMethod))
			return false;
		if (rankInUrl == null) {
			if (resourceParam.rankInUrl != null)
				return false;
		} else if (!rankInUrl.equals(resourceParam.rankInUrl))
			return false;
		if (value == null) {
			if (resourceParam.value != null)
				return false;
		} else if (!value.equals(resourceParam.value))
			return false;
		return true;
	}

	private static boolean hasSupportedType(Parameter parameter) {
		if (parameter == null) {
			return false;
		}
		
		Class<?> clazz = Primitives.unwrap(parameter.getType());

		return (clazz.isPrimitive() && clazz != void.class && clazz != char.class) || clazz == String.class || clazz.isEnum();
	}
	
	public Integer getRankInUrl() {
		return rankInUrl;
	}

	public void setRankInUrl(Integer rankInUrl) {
		this.rankInUrl = rankInUrl;
	}

	public static class NotSupportedException extends Exception {
		private static final long serialVersionUID = 7015933353596207604L;

		public NotSupportedException() {
			super();
		}

		@Override
		public String getMessage() {
			return "Parameter\'s type is not supported. Only numeric types,enums and strings are supported";
		}
		
	}
	
	
	
}
