package server.configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.google.common.primitives.Primitives;

import server.annotation.PARAM;

public class ResourceParam {
	private Parameter parameter;
	private Integer rank;
	private Object value;
	
	public ResourceParam(Parameter parameter, Integer rank) throws NotSupportedException {
		super();
		setParameter(parameter);
		this.rank = rank;
	}

	public ResourceParam() {
		super();
	}

	public boolean isBody() {
		return parameter.getAnnotation(PARAM.class) == null;
	}
	
	public String getAnnotationValue() {
		PARAM param = parameter.getAnnotation(PARAM.class);
		return param != null ? param.value():null;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = valueOf(value);
	}
	
	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public void setParameter(Parameter parameter) throws NotSupportedException {
		if (!hasSupportedType(parameter)) {
			throw new NotSupportedException();
		}
		
		this.parameter = parameter;
	}

	/**
	 * TODO
	 * @return
	 */
	public boolean isDigit() {
		return parameter.getClass() == null;
	}
	
	public boolean isNumeric() {
		Class<?> clazz = Primitives.wrap(parameter.getType());
		return isNumericClass(clazz);
	}
	
	private boolean isNumericClass(Class<?> clazz) {
		if (clazz == Object.class) {
			return false;
		} else if (clazz == Number.class) {
			return true;
		}
		
		return isNumericClass(clazz.getSuperclass());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parameter == null) ? 0 : parameter.hashCode());
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
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
		ResourceParam other = (ResourceParam) obj;
		if (parameter == null) {
			if (other.parameter != null)
				return false;
		} else if (!parameter.equals(other.parameter))
			return false;
		if (rank == null) {
			if (other.rank != null)
				return false;
		} else if (!rank.equals(other.rank))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;	
		return true;
	}	
	
	private Object valueOf(String value) {
		
		Class<?> clazz = Primitives.wrap(parameter.getType());
		
		if ( clazz == String.class) {
			return value;
		}
		
		try {
			Method  method = clazz.getMethod("valueOf",String.class);
			return method.invoke(null, value); // valueOf is static
			
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			
		}
		return null;
	}
	
	private static boolean hasSupportedType(Parameter parameter) {
		
		if (parameter == null) {
			return false;
		}
		
		Class<?> clazz = Primitives.unwrap(parameter.getType());

		return (clazz.isPrimitive() && clazz != void.class && clazz != char.class) || clazz == String.class;
	}
	
	public static class NotSupportedException extends Exception {
		private static final long serialVersionUID = 7015933353596207604L;

		public NotSupportedException() {
			super();
		}

		@Override
		public String getMessage() {
			return "Parameter\'s type is not supported. Only numeric types and strings are supported";
		}
		
	}
}
