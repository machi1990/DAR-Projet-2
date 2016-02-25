package com.upmc.stl.dar.server.resource.configuration;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.upmc.stl.dar.server.annotation.CONSUMES;
import com.upmc.stl.dar.server.annotation.DELETE;
import com.upmc.stl.dar.server.annotation.GET;
import com.upmc.stl.dar.server.annotation.PATCH;
import com.upmc.stl.dar.server.annotation.PATH;
import com.upmc.stl.dar.server.annotation.POST;
import com.upmc.stl.dar.server.annotation.PRODUCES;
import com.upmc.stl.dar.server.annotation.PUT;
import com.upmc.stl.dar.server.request.ContentType;
import com.upmc.stl.dar.server.request.Request;
import com.upmc.stl.dar.server.request.UrlParameters;
import com.upmc.stl.dar.server.resource.configuration.ResourceParam.NotSupportedException;
import com.upmc.stl.dar.server.response.Jsonfier;
import com.upmc.stl.dar.server.response.Response;
import com.upmc.stl.dar.server.response.Status;

/**
 * TODO 
 * <ol>
 * 		<li> &nbsp Full implementation of arguments </li>
 *      <li> &nbsp Generate a regular expression pattern which will be used in the matches mathod. </li>
 *      <li> &nbsp equals and hashCode re-implementation using the generated pattern. </li>
 *      <li> &nbsp Full review and correction </li>
 * </ol> 
 * <br>
 * @author Machi
 *
 */
public class Resource {
	private String url;
	private Method method;
	private Class<?> clazz;
	private Parameter[] parameters;
	private com.upmc.stl.dar.server.request.Method requestMethod;

	private ArrayList<Parameter> annotatedParameters = new ArrayList<>();
	
	private Map<String,ResourceParam> mapper = new HashMap<>();
	private Map<Integer,ResourceParam> nonAnnotatedMapper = new HashMap<>();
	
	private Pattern pattern;
	
	public Resource(Class<?> clazz, Method method) throws NotSupportedException, ParamConflictException {
		super();
		setClazz(clazz);
		this.setMethod(method);
	}

	public Resource() {
		super();
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) throws NotSupportedException, ParamConflictException {
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
	 * TODO retrieve all meta-data information e.g The method Annotation such as
	 * GET POST etc To make sure a correct request method is invoked.
	 * @throws NotSupportedException 
	 * @throws ParamConflictException 
	 */
	private void retrieveMetaInfos() throws NotSupportedException, ParamConflictException {
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
		
		retrieveParameters(method);
	}

	private void retrieveParameters (Method method) throws NotSupportedException, ParamConflictException {
		this.setParamters(method.getParameters());
		
		Parameter parameter;
		for (Integer index = 0; index < parameters.length; ++index ) {
			parameter = parameters[index];
			ResourceParam param = new ResourceParam(parameter, index);
			
			if (param.hasAnnotation()) { // is annotated
				annotatedParameters.add(parameter);
				
				if (mapper.containsKey(param.getAnnotationValue())) {
					throw new ParamConflictException();
				}
				
				mapper.put(param.getAnnotationValue(), param);
				
			} else {
				nonAnnotatedMapper.put(index, param);
			}
		}
	}
	
	private boolean isVoid() {
		return method.getReturnType().equals(Void.TYPE);
	}

	/**
	 * @param instance
	 * @param args
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws NotMatchedException
	 * @throws IOException 
	 */
	public Object invoke(Request request) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, InstantiationException, NotMatchedException, IOException {
		Integer index = request.getResourceUrl().indexOf("?");

		String requestUrl = index != -1 ? request.getResourceUrl().substring(0, index) : request.getResourceUrl();
		UrlParameters urlParams = UrlParameters.newInstance();

		if (!matches(request.getMethod()) || !matches(requestUrl)) {
			throw new NotMatchedException();
		}

		if (index != -1) {
			UrlParameters.putParamsTo(urlParams,request.getResourceUrl().substring(index + 1));
		}
		
		Object result = method.invoke(clazz.newInstance(),arguments(requestUrl,request,urlParams));

		if (result instanceof Response) {
			return result;
		}

		Response response = Response.response(Status.OK);

		if (isVoid()) {
			return response;
		}

		/**
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
	 * 
	 * @param url
	 * @return
	 */
	private boolean matches(String url) {
		String[] path = url.split("/");
		String[] pathTemplate = this.url.split("/");
		
		
		// /echo/6456/p -> path example
		// /echo/<id>/p -> pathTemplate example

		// Match application name
		if (pathTemplate[0].equals(path[0])) {
			for (int i = 1; i < pathTemplate.length; i++) {
				// Test if parameter
				if (pathTemplate[i].startsWith("<")) {
					for (Parameter p : annotatedParameters) {
						if (p.getName().equals(pathTemplate[i])) {
							// if return true, Set value path[i] to parameter
							if (p.getType().equals(Long.class) && isInteger(path[i])) {
								Long.valueOf(path[i]);
							} else if (p.getType().equals(Integer.class) && isInteger(path[i])) {
								Integer.valueOf(path[i]);
							} else if (p.getType().equals(Double.class) && isInteger(path[i])) {
								Double.valueOf(path[i]);
							} else if (p.getType().equals(String.class)) { // impossible
																			// avec
																			// notre
																			// mod�le
								// path[i];
							} else {
								return false;
							}
						}
					}
				} else if (!pathTemplate[i].equals(path[i])) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public static boolean isInteger(String s) {
		return isInteger(s, 10);
	}

	private static boolean isInteger(String s, int radix) {
		if (s.isEmpty())
			return false;
		for (int i = 0; i < s.length(); i++) {
			if (i == 0 && s.charAt(i) == '-') {
				if (s.length() == 1)
					return false;
				else
					continue;
			}
			if (Character.digit(s.charAt(i), radix) < 0)
				return false;
		}
		return true;
	}

	private boolean matches(com.upmc.stl.dar.server.request.Method requestMethod) {
		return this.requestMethod.equals(requestMethod);
	}

	private boolean producesJSON() {
		PRODUCES produces = method.getAnnotation(PRODUCES.class);
		return produces != null && produces.value().equals(ContentType.JSON);
	}

	private boolean consumesJSON () {
		CONSUMES consumes = method.getAnnotation(CONSUMES.class);
		return consumes != null && consumes.value().equals(CONSUMES.Consumed.JSON);
	}
	
	/**
	 * TODO
	 * Return a list of arguments values in order
	 * e.g by parsing the demanded resourceUrl at the same time querying the parameters list
	 * to get the route-param to Parameter match before invoking the method. 
	 * See arguments
	 * @param request
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	private Object[] arguments(String url,Request request,UrlParameters params) throws JsonParseException, JsonMappingException, IOException {
		Object[] arguments = new Object[this.parameters.length];
		
		for (Integer index: nonAnnotatedMapper.keySet()) {
			Class<?> type = nonAnnotatedMapper.get(index).getType();
			if (type == Request.class) {
				arguments[index] = request;
			} else if (type == UrlParameters.class) {
				arguments[index] = params;
			} else { // Body
				if (consumesJSON()) {
					arguments[index] = Jsonfier.toJavaObject(request.getBody(), type); // JSON
				} else {
					arguments[index] = request.getBody(); // STRING
				}
			}
		}
		
		//TODO parse annotated params

		return arguments;
	}

	private static com.upmc.stl.dar.server.request.Method method(Method method) {

		Annotation[] annotations = method.getAnnotations();
		Class<?> annotationClass;

		for (Annotation annotation : annotations) {
			annotationClass = annotation.annotationType();

			if (annotationClass == GET.class) {
				return com.upmc.stl.dar.server.request.Method.GET;
			}

			if (annotationClass == PUT.class) {
				return com.upmc.stl.dar.server.request.Method.PUT;
			}

			if (annotationClass == POST.class) {
				return com.upmc.stl.dar.server.request.Method.POST;
			}
			if (annotationClass == DELETE.class) {
				return com.upmc.stl.dar.server.request.Method.DELETE;
			}

			if (annotationClass == PATCH.class) {
				return com.upmc.stl.dar.server.request.Method.PATCH;
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
			if (annotation.annotationType().equals(PATH.class) 
					|| annotation.annotationType().equals(POST.class)
					|| annotation.annotationType().equals(PUT.class) 
					|| annotation.annotationType().equals(GET.class)
					|| annotation.annotationType().equals(DELETE.class)
					|| annotation.annotationType().equals(PATCH.class)) {
				return true;
			}
		}

		return false;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

}
