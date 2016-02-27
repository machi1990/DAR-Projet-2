package com.upmc.stl.dar.server.resource.configuration;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
import com.upmc.stl.dar.server.exceptions.BadFormedUrlException;
import com.upmc.stl.dar.server.exceptions.ExceptionCreator;
import com.upmc.stl.dar.server.exceptions.ExceptionCreator.ExceptionKind;
import com.upmc.stl.dar.server.exceptions.ServerException;
import com.upmc.stl.dar.server.exceptions.NotSupportedException;
import com.upmc.stl.dar.server.exceptions.ParamConflictException;
import com.upmc.stl.dar.server.exceptions.ParamNotAcceptableException;
import com.upmc.stl.dar.server.exceptions.UrlParamConflictException;
import com.upmc.stl.dar.server.request.ContentType;
import com.upmc.stl.dar.server.request.Request;
import com.upmc.stl.dar.server.request.UrlParameters;
import com.upmc.stl.dar.server.response.Jsonfier;
import com.upmc.stl.dar.server.response.Response;
import com.upmc.stl.dar.server.response.Status;

/**
 * TODO 
 * <ol>
 * 		<li> &nbsp Full review and correction </li>
 * </ol> 
 * <br>
 * @author Machi
 *
 */
public class Resource {
	private String url;
	private Pattern pattern;
	private Method method;
	private Class<?> clazz;
	
	private String[] accessors = new String[0];
	private com.upmc.stl.dar.server.request.Method requestMethod;

	private Map<Integer,ResourceParam> annotatedParamsMapper = new HashMap<>();
	private Map<Integer,ResourceParam> nonAnnotatedParamsMapper = new HashMap<>();
	
	protected Resource(Class<?> clazz, Method method) throws ServerException {
		super();
		setClazz(clazz);
		setMethod(method);
	}

	protected Resource() {
		super();
	}

	private void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	private void setMethod(Method method) throws ServerException {
		this.method = method;
		this.requestMethod = method(method);
		this.retrieveMetaInfos();
	}

	private void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Retrieves all meta-data information e.g The method Annotation such as
	 * GET POST etc To make sure a correct request method is invoked.
	 * @throws NotSupportedException 
	 * @throws ParamConflictException 
	 * @throws ParamNotAcceptableException 
	 */
	private void retrieveMetaInfos() throws ServerException {
		if (method == null) {
			throw new IllegalAccessError("Method must be initialized");
		}

		if (!method.isAccessible()) {
			method.setAccessible(true);
		}
		
		retrieveParameters(method);
	}

	private void retrieveParameters (Method method) throws ServerException {
		PATH path = clazz.getAnnotation(PATH.class);

		if (!isAcceptableClassUrl(path.value())) {
			throw ExceptionCreator.creator().create(ExceptionKind.BAD_FORMED_URL,path.value(), null, clazz);
		}
		
		setUrl(path.value()+getMethodUrl());
		
		Map<String,ResourceParam> preAnnotatedParamsMapper = new HashMap<>();
		
		filterParameters(preAnnotatedParamsMapper);
		paramsParser(preAnnotatedParamsMapper);
	}
	
	private String getMethodUrl() throws ServerException  {
		PATH path = method.getAnnotation(PATH.class);

		if (path == null) {
			return "";
		}
			
		if (path.value().isEmpty() || !isAcceptableMethodUrl(path.value())) {
			throw ExceptionCreator.creator().create(ExceptionKind.BAD_FORMED_URL,path.value(), method, clazz);
		}
		
		return path.value();
	}
	
	private void filterParameters(Map<String,ResourceParam> preAnnotatedParamsMapper) throws ServerException {
		Parameter[] parameters = method.getParameters();
		Parameter parameter;
		
		for (Integer index = 0; index < parameters.length; ++index ) {
			parameter = parameters[index];
			ResourceParam param = new ResourceParam(parameter, index);
			
			if (param.hasAnnotation()) { // is annotated
				String value = param.getAnnotationValue();
				
				if (!isAcceptableParamUrl(value)) {
					throw ExceptionCreator.creator().create(ExceptionKind.PARAM_NOT_ACCEPTABLE,value,method,clazz);
				}
				
				if (preAnnotatedParamsMapper.containsKey(value)) {
					throw ExceptionCreator.creator().create(value, preAnnotatedParamsMapper.get(value).getName(), parameter.getName(), method, clazz);
				}
				
				preAnnotatedParamsMapper.put(value, param);
				
			} else {
				nonAnnotatedParamsMapper.put(index, param);
			}
		}
	}
	
	/** 
	 * Check whether a class has a well defined path url value
	 * @param url
	 * @return
	 */
	private boolean isAcceptableClassUrl(String url) {
		return url.matches("/\\w+(/\\w+)*\\w+");
	}
	
	/** 
	 * Check whether a method has a well defined path url value
	 * @param url
	 * @return
	 */
	private boolean isAcceptableMethodUrl(String url) {
		return url.matches("/|(/(\\w+|<(\\w[\\w-]*)>))*");
	}
	
	/** 
	 * Check whether a param has a well defined path url value
	 * @param url
	 * @return
	 */
	private boolean isAcceptableParamUrl(String url) {
		return url.matches("<(\\w[\\w-]*)>");
	}
	
	/**
	 * A parser of params to create a map of params in order of their 
	 * declaration in the url. <br>
	 * 
	 * Also we'll create a pattern in this method.
	 * 
	 * @param mapper
	 * @throws UrlParamConflictException 
	 * @throws BadFormedUrlException 
	 */
	private void paramsParser(Map<String,ResourceParam> mapper) throws ServerException {
		if (annotatedParamsMapper.isEmpty()) {
			pattern = Pattern.compile(url);
		}
		
		accessors = url.split("/");
		Map<String,Boolean> treatedParams = new HashMap<>();
		StringBuilder pattern = new StringBuilder("");
		
		Integer counter = 0;
		ResourceParam param;
		String accessor;
		
		for (Integer index = 1; index < accessors.length ; ++index) {
			accessor = accessors[index];
			
			if (accessor.matches("\\w*")) {
				pattern.append("/"+accessor); 
				continue;
			}
			
			if (!mapper.containsKey(accessor)) {
				throw ExceptionCreator.creator().create(ExceptionKind.BAD_FORMED_URL,url, method, clazz);
			}
		
			if (treatedParams.containsKey(accessor)) {
				throw ExceptionCreator.creator().create(ExceptionKind.URL_PARAM_CONFLICT,accessor, method, clazz);
			}
			
			param = mapper.get(accessor);
			param.setRankInUrl(counter);
			annotatedParamsMapper.put(counter, param);
			treatedParams.put(accessor, true);
			
			pattern.append("/"+param.getPattern());
			counter++;
		}
		
		this.pattern = Pattern.compile(pattern.toString());
		
		/**
		 * Last check to verify if a correct mapping has been done.
		 * If the two maps are different in size then no correct mapping was done
		 */
		
		if (annotatedParamsMapper.size() != mapper.size()) {
			throw ExceptionCreator.creator().create(ExceptionKind.BAD_FORMED_URL,url, method, clazz);
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
	 * @throws IOException 
	 * @throws ServerException 
	 */
	public Object invoke(Request request) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, InstantiationException, IOException, ServerException {
		
		String url = request.getUrl();
		UrlParameters urlParams = UrlParameters.newInstance();

		if (!matches(request.getMethod()) || !matches(url)) {
			throw ExceptionCreator.creator().create(ExceptionKind.NOT_MATCHED);
		}

		UrlParameters.putParamsTo(urlParams,request.getUrlParams());
		
		Object result = method.invoke(clazz.newInstance(),arguments(url,request,urlParams));

		if (result instanceof Response) {
			return result;
		}

		Response response = Response.response(Status.OK);

		if (isVoid()) {
			return response;
		}

		/**
		 * TODO  let a filter to do this work.
		 */

		if (producesJSON()) {
			response.setContentType(ContentType.JSON);
			response.build(Jsonfier.jsonfy(result));
		} else {
			response.build(result);
		}
		
		return response;
	}

	private boolean matches(String url) {
		return pattern.matcher(url).matches();
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
	 * Return a list of arguments values in order. 
	 * By parsing the demanded resourceUrl at the same time querying the parameters list
	 * to get the route-param-to-parameter match before invoking the method. 
	 * @param request
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	private Object[] arguments(String url,Request request,UrlParameters params) throws JsonParseException, JsonMappingException, IOException, IllegalAccessException, IllegalArgumentException {
		Object[] arguments = new Object[this.nonAnnotatedParamsMapper.size()+annotatedParamsMapper.size()];
		
		for (Integer index: nonAnnotatedParamsMapper.keySet()) {
			Class<?> type = nonAnnotatedParamsMapper.get(index).getType();
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
		
		if (annotatedParamsMapper.isEmpty()) {
			return arguments;
		}
		
		String urlValues[] = url.split("/");
		String accessor,value;
		Integer counter = 0;
		
		for (Integer index = 0; index < accessors.length ; ++index) {
			accessor = accessors[index];
			value = urlValues[index];
			
			if (accessor.equals(value)) { // is not a url param.
				continue;
			}
		
			ResourceParam param = annotatedParamsMapper.get(counter);	
			arguments[param.getRankInMethod()] = param.valueOf(value);
			counter++;
		}
	

		return arguments;
	}

	/**
	 * Converts annotation method to enum.
	 * @param method
	 * @return
	 */
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
		return "Resource [method= \"" + method.getName() + " \" of , class= \"" + clazz.getName() + "\" ]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pattern == null) ? 0 : pattern.pattern().hashCode());
		result = prime * result + ((requestMethod == null) ? 0 : requestMethod.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null)
			return false;
		if (getClass() != object.getClass())
			return false;
		Resource resource = (Resource) object;
		if (pattern == null) {
			if (resource.pattern != null)
				return false;
		} else if (!pattern.pattern().equals(resource.pattern.pattern()))
			return false;
		if (requestMethod != resource.requestMethod)
			return false;
		return true;
	}

	protected static boolean hasLocalAnnotation(Method method) {
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
}
