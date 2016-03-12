package com.upmc.stl.dar.server.configuration.views;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.NoSuchAttributeException;

import com.upmc.stl.dar.server.exceptions.ExceptionCreator;
import com.upmc.stl.dar.server.exceptions.ExceptionCreator.ExceptionKind;
import com.upmc.stl.dar.server.exceptions.ServerException;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Element;

public class ViewElement {
	private Element element;
	private List<ViewElement> children = new ArrayList<>();

	protected ViewElement(Element element) {
		super();
		this.element = element;

		for (Element subElm : element.getChildElements()) {
			children.add(new ViewElement(subElm));
		}
	}

	/**
	 * TODO
	 * @param globalVariables2 
	 */
	public String build(final String template, Map<String, Object> globalVariables) throws Exception{
		if (needDyamicRendering()) {
			return evaluate(template,globalVariables);
		}
		
		return stringfyElement(template, globalVariables);
	}

	private String stringfyElement(final String template, Map<String, Object> globalVariables)
			throws Exception {

		String content = element.getContent().toString();
		
		if (content.isEmpty()) {
			return toString();
		}
		
		StringBuilder attrs = new StringBuilder();
		for (Attribute attr : element.getAttributes()) {
			attrs.append(" " + attr);
		}
		
		StringBuilder builder = new StringBuilder("<" + element.getName() + attrs + ">");

		
		Integer counter = 0;
		Integer index = 0;

		while (counter < content.length()) {
			char c = content.charAt(counter);
			if (c != '<') {
				builder.append(c);
				counter++;
			} else {
				ViewElement elt = children.get(index++);
				builder.append(elt.build(template,globalVariables));
				counter += elt.contentLength();
			}
		}

		builder.append("</" + element.getName() + ">");
		return builder.toString();
	}


	public Integer contentLength() {
		StringBuilder attrs = new StringBuilder();

		for (Attribute attr : element.getAttributes()) {
			attrs.append(attr);
		}
		
		return ("<" + element.getName() + attrs + ">" + element.getContent().toString() + "</" + element.getName()
				+ ">").length() + element.getAttributes().size();
	}
	
	private String evaluate(final String template,Map<String, Object> globalVariables) throws Exception {
		Map<String,String> attrs = new HashMap<>();
		

		for (Attribute attr : element.getAttributes()) {
			attrs.put(attr.getKey().trim().toLowerCase(), attr.getValue().trim().replace("\\s{2,}", ""));
		}
		
		if (attrs.size() > 2) {
			throw ExceptionCreator.creator().create(ExceptionKind.EVAL);
		} 
		
		if (attrs.containsKey("if")) {
			return evaluateIf(template, globalVariables, attrs);
		} else if (attrs.containsKey("for")) {
			return evaluateFor(template,globalVariables,attrs);
		}
		
		return localEvaluate(template, globalVariables);

	}

	private String evaluateFor(final String template, Map<String, Object> globalVariables,Map<String, String> attrs) throws Exception {
		// TODO here
		String forExpr= attrs.get("for").replaceAll("\\s+", " ").trim();
		
		if (!matchesFor(forExpr)) {
			return "";
		} 
		
		String expres[] = forExpr.split("\\s(in)\\s");
		
		Object globals = getValue(expres[1].trim(), globalVariables);
		StringBuilder builder = new StringBuilder();
		Map<String,Object> env = new HashMap<>();
		
		if (globals instanceof Iterable<?>) {	
			Iterable<?> iterator = (Iterable<?>)globals;
			for (Object local:iterator) {
				env.putAll(globalVariables);
				env.put(expres[0].trim(),local);
				builder.append(localEvaluate(template, env));
				env.clear();
			}
			return builder.toString();
			
		} else if (globals.getClass().isArray()) {
			Object[] globalsArray = (Object[])globals;
			for (Object local:globalsArray) {
				env.putAll(globalVariables);
				env.put(expres[0].trim(),local);
				builder.append(localEvaluate(template, env));
				env.clear();
			}
			
			return builder.toString();
		}
		
		env.putAll(globalVariables);
		env.put(expres[0], globals);
		
		return localEvaluate(template,env);
	}

	private String evaluateIf(final String template, Map<String, Object> globalVariables, Map<String, String> attrs)
			throws ServerException, NoSuchAttributeException, NoSuchFieldException, IllegalAccessException, Exception {
		String condExpr = attrs.get("if").replace("\\s+", "");
		
		if (!matches(condExpr)) {
			throw ExceptionCreator.creator().create(ExceptionKind.EVAL);
		}
		
		Object condition = getValue(condExpr, globalVariables);
		
		if (condition instanceof Boolean) {
			Boolean cond = (Boolean) condition;
			if (cond) {
				return localEvaluate(template, globalVariables);
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	private String localEvaluate(String template,Map<String, Object> globalVariables) throws Exception {
		StringBuilder stream = new StringBuilder();
		String content = element.getContent().toString();

		Integer counter = 0;
		Integer index = 0;

		while (counter < content.length()) {
			char c = content.charAt(counter);
			
			if (c != '<') {
				if (c == '{') {
					counter +=2;
					StringBuilder expr = new StringBuilder();
					while (counter < content.length() && content.charAt(counter) != '}') {
						expr.append(content.charAt(counter++));
					}
					
					String expression = expr.toString().replace("\\s+", "");
					
					if (!matches(expression)) {
						throw ExceptionCreator.creator().create(ExceptionKind.EVAL,template,expression);
					}
					
					if (counter < content.length()) {
						counter += 2;
					}
					
					stream.append(getValue(expression, globalVariables));
				} else {
					stream.append(c);
					counter++;
				}
			} else {
				ViewElement elt = children.get(index++);
				stream.append(elt.build(template, globalVariables));
				counter += elt.contentLength();
			}
		}
		
		return stream.toString();
	}
	/**
	 * 
	 * @param expression
	 * @param globalVariables
	 * @return
	 * @throws NoSuchAttributeException
	 * @throws ServerException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private Object getValue(String expression,Map<String,Object> globalVariables) throws NoSuchAttributeException, ServerException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		String[] splittedExpression = expression.split("\\.");
		
		if (splittedExpression.length == 1) {
			return getVariable(splittedExpression[0], globalVariables);
		} else {
			Object evaluationResult = getVariable(splittedExpression[0], globalVariables);
			for (Integer i = 1 ; i < splittedExpression.length; ++i) {
				
				if (evaluationResult == null) {
					throw ExceptionCreator.creator().create(ExceptionKind.EVAL);
				}
				
				evaluationResult = getFieldValue(evaluationResult, splittedExpression[i]);
			}
			
			return evaluationResult;
		}
	}
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private String getExpression() {
		String content = element.getContent().toString();
		Integer startIndex = content.indexOf("{{");
		Integer endIndex = content.indexOf("}}");
		
		return content.substring(startIndex+2, endIndex).trim();
	}
	
	private boolean needDyamicRendering() {
		return element.getName().equals("render");
	}
	
	/**
	 * 
	 * @param target
	 * @param methodname
	 * @param args
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchAttributeException
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private Object invoke(String target,String methodname,Map<String,Object> env,String ...args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchAttributeException {
		Object[] arguments = new Class[args.length];
		
		for (Integer  index = 0; index < args.length; ++index) {
			Object argument = getVariable(args[index],env);
			arguments[index] = argument;
		}
		
		return invoke(target, methodname, arguments);
	}
	
	/**
	 * 
	 * @param target
	 * @param methodname
	 * @param args
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchAttributeException
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private Object invoke(String target,String methodname, Map<String,Object> env,Object ...args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchAttributeException {
		return invoke(getVariable(target,env),methodname,args);
	}
	
	/**
	 * 
	 * @param target
	 * @param methodname
	 * @param args
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	@Deprecated
	private Object invoke (Object target, String methodname, Object ...args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?>[] classes = new Class[args.length];
		
		for (Integer  index = 0; index < args.length; ++index) {
			classes[index] = args[index].getClass();
		}
		
		return getMethod(target, methodname, classes).invoke(target, args);
	}
	
	/**
	 * 
	 * @param target
	 * @param globalVariables 
	 * @return
	 * @throws NoSuchAttributeException
	 */
	private Object getVariable(String expression, Map<String, Object> globalVariables) throws NoSuchAttributeException {
		String target = expression.trim();
		if (globalVariables.containsKey(target)) {
			return globalVariables.get(target);
		}  else {
			throw new NoSuchAttributeException();
		}
	}
	/**
	 * 
	 * @param target
	 * @param methodname
	 * @param arguments
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private Method getMethod(Object target, String methodname,Class<?> ...arguments) throws NoSuchMethodException, SecurityException {
		Method method = target.getClass().getMethod(methodname, arguments);
		method.setAccessible(true);
		return method;
	}
	
	/**
	 * 
	 * @param target
	 * @param fieldName
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchAttributeException
	 */
	@SuppressWarnings("unused")
	private Object getFieldValue(String target,String fieldName,Map<String,Object> env) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchAttributeException {
		return getFieldValue(getVariable(target,env), fieldName);
	}
	
	/**
	 * 
	 * @param target
	 * @param fieldName
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private Object getFieldValue(Object target,String fieldName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = target.getClass().getField(fieldName);
		field.setAccessible(true);
		return field.get(target);
	}
	
	/**
	 * 
	 * @param target
	 * @param fieldName
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unused")
	private String stringfyFieldValue(Object target,String fieldName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		return getFieldValue(target, fieldName).toString();	
	}
	
	/**
	 * @param expression
	 * @return
	 */
	private boolean matches(String expression) {
		return expression.replaceAll("\\s+", "").matches("\\w+(\\.\\w+)*");
	}

	/**
	 * @param expression
	 * @return
	 */
	private boolean matchesFor(String expression) {
		return expression.replaceAll("\\s+", " ").trim().matches("\\w+\\s(in)\\s(\\w+(\\.\\w+)*)");
	}
	
	@Override
	public String toString() {
		return  element.toString();
	}
}
