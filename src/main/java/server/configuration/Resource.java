package server.configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import request.ContentType;
import request.Request;
import request.UrlParameters;
import response.Jsonfier;
import response.Response;
import response.Status;
import server.annotation.DELETE;
import server.annotation.GET;
import server.annotation.PARAM;
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
	private Parameter[] paramters;
	private String url;
	private request.Method requestMethod;
	private HashMap<String,Object> requestParameters = new HashMap<String,Object>();
	
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
		return paramters;
	}

	public void setParamters(Parameter[] paramters) {
		this.paramters = paramters;
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
		 * System.out.println(params); 
		 * TODO do something with params
		 */
		
		// String url = request.getResourceUrl();

		/**
		 * TODO parse the demanded resourceUrl to get their corresponding match
		 * to the method parameters
		 */

		Object result;
		if(requestParameters.isEmpty()){
			result = method.invoke(clazz.newInstance(), request);
		} else {
			System.out.println("invoke.invokeMethodWithParameters");
			result = invokeMethodWithParameters();
		}
		
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
		String[] path = url.split("/");
		String[] pathTemplate = this.url.split("/");
		ArrayList<Parameter> params = new ArrayList<Parameter>();
		
		if(path.length != pathTemplate.length){
			return false;
		}
		// Check if annoted param
		for(int i=0; i< paramters.length; i++){
			if (paramters[i].getAnnotation(PARAM.class) != null){
				params.add(paramters[i]);
			}
		}
		
		// /echo/6456/p -> path example
		// /echo/<id>/p -> pathTemplate example
		System.out.println("Template = " + this.url);
		System.out.println("Path = " + url);
		//Match application name
		if(pathTemplate[0].equals(path[0])){
			for(int i=1; i< pathTemplate.length; i++){
				// Test if parameter
				if(pathTemplate[i].startsWith("<")){
					System.out.println(".startsWith(<)");
					boolean paramFound = false;
					for(Parameter p : params){
						System.out.println("p.annotation(PARAM)="+p.getAnnotationsByType(PARAM.class)[0].value()+" / pathTemplate="+pathTemplate[i]);
						if(p.getAnnotationsByType(PARAM.class)[0].value().equals(pathTemplate[i])){
							System.out.println("ok");
							// if return true, Set value path[i] to parameter
							if(p.getType().equals(Long.class) && isInteger(path[i])){
								requestParameters.put(pathTemplate[i],Long.valueOf(path[i]));
								paramFound = true;
								break;
							} else if(p.getType().equals(Integer.class) && isInteger(path[i])){
								requestParameters.put(pathTemplate[i],Integer.valueOf(path[i]));
								paramFound = true;
								break;
							} else if(p.getType().equals(Double.class) && isInteger(path[i])){
								requestParameters.put(pathTemplate[i],Double.valueOf(path[i]));
								paramFound = true;
								break;
							} else if(p.getType().equals(String.class)){ // impossible avec notre modèle
								//path[i];
							} else {
								return false;
							}
						}
					}
					if(!paramFound){
						return false;
					}
				} else if(!pathTemplate[i].equals(path[i])){
					return false;
				}
			}
			System.out.println("return true, requestParam : "+requestParameters.size());
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	private static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	
	private Object invokeMethodWithParameters() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		ArrayList<Object> paramsInOrder = new ArrayList<Object>();
		ArrayList<String> paramsName = new ArrayList<String>();
		for(int i=0; i<paramters.length; i++){
			if (paramters[i].getAnnotation(PARAM.class) != null){
				paramsName.add(paramters[i].getAnnotationsByType(PARAM.class)[0].value());
			}
		}
		
		for(String n : paramsName){
			if(requestParameters.containsKey(n)){
				paramsInOrder.add(requestParameters.get(n));
			}
		}
		if(paramsInOrder.size() == 0){
			throw new IllegalArgumentException();
		}
		System.out.println("paramsInOrder ="+paramsInOrder.get(0));
		
		switch(paramsInOrder.size()){
		case 1 :
			return method.invoke(clazz.newInstance(), paramsInOrder.get(0));
		case 2 :
			return method.invoke(clazz.newInstance(), paramsInOrder.get(0), paramsInOrder.get(1));
		case 3 :
			return method.invoke(clazz.newInstance(), paramsInOrder.get(0), paramsInOrder.get(1), paramsInOrder.get(2));
		case 4 :
			return method.invoke(clazz.newInstance(), paramsInOrder.get(0), paramsInOrder.get(1), paramsInOrder.get(2), paramsInOrder.get(3));
		case 5 :
			return method.invoke(clazz.newInstance(), paramsInOrder.get(0), paramsInOrder.get(1), paramsInOrder.get(2), paramsInOrder.get(3), paramsInOrder.get(4));
		default :
			throw new IllegalArgumentException();
		}
		// return method.invoke(clazz.newInstance(), paramsInOrder); Doesn't work
	}
	
	private boolean matches(request.Method requestMethod) {
		return this.requestMethod.equals(requestMethod);
	}
	
	private boolean producesJSON () {
		PRODUCES produces = method.getAnnotation(PRODUCES.class);
		return produces != null && produces.value().equals(ContentType.JSON);
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
		result = prime * result + Arrays.hashCode(paramters);
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
		if (!Arrays.equals(paramters, resource.paramters))
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
