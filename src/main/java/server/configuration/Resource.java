package server.configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import request.Request;
import response.Response;
import response.Status;
import server.annotation.PATH;

public class Resource {
	private Method method;
	private String url;
	private Parameter[] paramters;
	
	public Resource(String url,Method method) {
		super();
		this.setUrl(url);
		this.setMethod(method);
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
		this.retrieveMetaInfos();
	}


	public Parameter[] getParamters() {
		return paramters;
	}

	public void setParamters(Parameter[] paramters) {
		this.paramters = paramters;
	}
	/**
	 * TODO
	 */
	private void retrieveMetaInfos() {
		if (method == null) {
			throw new IllegalAccessError("Method must be initialized");
		}
		
		if (!method.isAccessible()) {
			method.setAccessible(true);
		}
		
		PATH path = method.getAnnotation(PATH.class);
		
		if (path != null) {
			url += path.value();
		}
		
		this.setParamters(method.getParameters());
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	private boolean isVoid() {
		return method.getReturnType().equals(Void.TYPE);
	}
	
	/**
	 * TODO make sure the method is invoked with the right kind of arguments
	 * After the request is parsed, call this method with the generated request object and 
	 * response object.
	 * @param instance
	 * @param args
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Object invoke(Object instance,Request request)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		//String url = request.getResourceUrl();
		
		/**
		 * TODO
		 * parse the demanded resourceUrl to get their corresponding match to the method parameters
		 */
		
		Object result =  method.invoke(instance, request);
		
		if (result instanceof Response) {
			return result;
		}
		
		Response response = Response.response(Status.OK);
		
		if (isVoid()) {
			return response;
		}
		
		/**
		 * Filter the response according to the content type header.
		 * And the content-type method annotation.
		 * eg. if json 
		 * response.build(jsonMapper.writeValueAsString(result))
		 * 
		 * etc etc
		 */
		
		response.build(result);
		return response;
	}

	@Override
	public String toString() {
		return "Resource [method=" + method + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		Resource resource = (Resource) obj;
		if (method == null) {
			if (resource.method != null)
				return false;
		} else if (!method.equals(resource.method))
			return false;
		if (url == null) {
			if (resource.url != null)
				return false;
		} else if (!url.equals(resource.url))
			return false;
		return true;
	}
}
