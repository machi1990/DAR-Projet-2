package com.upmc.stl.dar.server.resource.configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javafx.util.Pair;

import com.google.common.primitives.Primitives;
import com.upmc.stl.dar.server.annotation.PARAM;
import com.upmc.stl.dar.server.exceptions.NotSupportedException;

public class ResourceParam {
	private Parameter parameter;
	private Integer rankInMethod;
	private Integer rankInUrl;
	
	private Object value;
	
	protected ResourceParam(Parameter parameter, Integer rank) throws NotSupportedException {
		super();
		setParameter(parameter);
		setRankInMethod(rank);
	}

	protected ResourceParam() {
		super();
	}
	
	protected String getAnnotationValue() {
		return hasAnnotation() ? parameter.getAnnotation(PARAM.class).value():null;
	}

	protected boolean hasAnnotation() {
		return parameter.getAnnotation(PARAM.class) != null;
	}

	protected Object getValue() {
		return value;
	}

	protected void setValue(String value) throws IllegalAccessException, IllegalArgumentException {
		this.value = valueOf(value);
	}
	
	protected Integer getRankInMethod() {
		return rankInMethod;
	}

	protected void setRankInMethod(Integer rankInMethod) {
		this.rankInMethod = rankInMethod;
	}

	protected void setParameter(Parameter parameter) throws NotSupportedException {
		this.parameter = parameter;
		
		if (hasAnnotation() && !hasSupportedType(parameter)) {
			throw new NotSupportedException();
		}
		
	}


	protected Object valueOf(String value) throws IllegalAccessException, IllegalArgumentException {
		
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
	
	protected Class<?> getType() {
		return Primitives.wrap(parameter.getType());
	}
	
	private boolean isDigitType() {
		Class<?> clazz = getType();
		return clazz == Long.class || clazz == Integer.class || clazz == Short.class || clazz == Byte.class;
	}
	
	private boolean isNumericType() {
		return Number.class.isAssignableFrom(Primitives.wrap(parameter.getType()));
	}
	
	protected Pair<String,String> toPattern() {
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
	
	protected Integer getRankInUrl() {
		return rankInUrl;
	}

	protected void setRankInUrl(Integer rankInUrl) {
		this.rankInUrl = rankInUrl;
	}

	
	protected String getName() {
		return parameter.getName();
	}
}
