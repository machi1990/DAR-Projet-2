package server.configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import request.ContentType;
import request.Request;
import request.UrlParameters;
import response.Jsonfier;
import response.Response;
import response.Status;
import server.annotation.DELETE;
import server.annotation.GET;
import server.annotation.PATCH;
import server.annotation.PATH;
import server.annotation.POST;
import server.annotation.PRODUCES;
import server.annotation.PUT;

/**
 * TODO Create a  Param class, from which we'll have a param object whose from data supplied by the PARAM annotation.
 *
 */
public class Resource {
	private Method method;
	private Class<?> clazz;
	private Parameter[] parameters;
	private String url;
	private request.Method requestMethod;
	
	public Resource(Class<?> clazz, Method method) {
		super();
		setClazz(clazz);
		this.setMethod(method);
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
		this.requestMethod = method(method);
		this.retrieveMetaInfos();
	}

	public Parameter[] getParamters() {
		return parameters;
	}

	public void setParamters(Parameter[] paramters) {
		this.parameters = paramters;
	}

	/**
	 * TODO retrieve all meta-data information e.g The method Annotation such as GET POST etc
	 * To make sure a correct request method is invoked. 
	 */
	private void retrieveMetaInfos() {
		if (method == null) {
			throw new IllegalAccessError("Method must be initialized");
		}

		
		if (!method.isAccessible()) {
			method.setAccessible(true);
		}

		PATH path = clazz.getAnnotation(PATH.class);

		setUrl(path.value());

		path = method.getAnnotation(PATH.class);

		if (path != null) {
			setUrl(getUrl() + path.value());
		}

		this.setParamters(method.getParameters());
	}

	private boolean isVoid() {
		return method.getReturnType().equals(Void.TYPE);
	}

	/**
	 * TODO make sure the method is invoked with the right kind of arguments
	 * After the request is parsed, call this method with the generated request
	 * object and response object.
	 * 
	 * @param instance
	 * @param args
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws NotMatchedException 
	 */
	public Object invoke(Request request)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NotMatchedException {
		Integer index = request.getResourceUrl().indexOf("?");
	
		String requestUrl =  index != -1 ? request.getResourceUrl().substring(0, index) : request.getResourceUrl();
		UrlParameters params = UrlParameters.newInstance();
		
		if (!matches(request.getMethod()) || !matches(requestUrl)) {
			throw new NotMatchedException();
		}
				
		if (index != -1) {
			params = UrlParameters.newInstance(request.getResourceUrl().substring(index+1));
		}
		
		/**
		 * TODO do something with params in arguments method
		 */

		/**
		 * TODO parse the demanded resourceUrl to get their corresponding match
		 * to the method parameters. 
		 * See arguments
		 */

		Object result = method.invoke(clazz.newInstance(),arguments(requestUrl,request,params));

		if (result instanceof Response) {
			return result;
		}

		Response response = Response.response(Status.OK);

		if (isVoid()) {
			return response;
		}

		/**
		 * Filter the response according to the content type header. And the
		 * content-type method annotation. eg. if json
		 * response.build(jsonMapper.writeValueAsString(result))
		 * 
		 * etc etc
		 * 
		 * TODO later make a filter to do this work.
		 */

		if (producesJSON()) {
			response.setContentType(ContentType.JSON);
			response.build(Jsonfier.jsonfy(result));
		} else {
			response.build(result);
		}
		return response;
	}

	
	/**
	 * TODO enhance the matching algorithms
	 * @param url
	 * @return
	 */
	private boolean matches(String url) {
		return this.url.equals(url);
	}
	
	private boolean matches(request.Method requestMethod) {
		return this.requestMethod.equals(requestMethod);
	}
	
	private boolean producesJSON () {
		PRODUCES produces = method.getAnnotation(PRODUCES.class);
		return produces != null && produces.value().equals(ContentType.JSON);
	}
	
	/**
	 * TODO
	 * Return a list of arguments values in order
	 * @param request
	 * @return
	 */
	private Object[] arguments(String url,Request request,UrlParameters params) {
		Object[] arguments = new Object[this.parameters.length];
		arguments[0] = request;
		
		return arguments;
	}
	
	private static request.Method method (Method method) {
		
		Annotation[] annotations = method.getAnnotations();
		Class<?> annotationClass;
		
		for (Annotation annotation:annotations) {
			annotationClass = annotation.annotationType();
			
			if (annotationClass == GET.class) {
				return request.Method.GET;
			}
			
			if (annotationClass == PUT.class) {
				return request.Method.PUT;
			}
			
			if (annotationClass == POST.class) {
				return request.Method.POST;
			}
			if (annotationClass == DELETE.class) {
				return request.Method.DELETE;
			}
			
			if (annotationClass == PATCH.class) {
				return request.Method.PATCH;
			}
		}
		
		return null;
		
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
		result = prime * result + Arrays.hashCode(parameters);
		result = prime * result + ((requestMethod == null) ? 0 : requestMethod.hashCode());
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
		if (!Arrays.equals(parameters, resource.parameters))
			return false;
		if (requestMethod != resource.requestMethod)
			return false;
		if (url == null) {
			if (resource.url != null)
				return false;
		} else if (!url.equals(resource.url))
			return false;
		return true;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public static boolean hasLocalAnnotation(Method method) {
		if (method == null) {
			return false;
		}

		Annotation[] annotations = method.getAnnotations();

		for (Annotation annotation : annotations) {
			if (annotation.annotationType().equals(PATH.class) || annotation.annotationType().equals(POST.class)
					|| annotation.annotationType().equals(PUT.class) || annotation.annotationType().equals(GET.class)
					|| annotation.annotationType().equals(DELETE.class) || annotation.annotationType().equals(PATCH.class)) {
				return true;
			}
		}

		return false;
	}

}
