package com.upmc.stl.dar.server.configuration.views;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.NoSuchAttributeException;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Element;

public class ViewElement {
	private Element element;
	private List<ViewElement> children = new ArrayList<>();

	private Map<String,Object> globalVariables = new HashMap<>();
	private Map<String,Object> localVaribales = new HashMap<>();
	
	public ViewElement(Element element) {
		super();
		this.element = element;

		for (Element subElm : element.getChildElements()) {
			children.add(new ViewElement(subElm));
		}
	}

	/**
	 * TODO
	 */
	@Override
	public String toString() {
		
		if (needDyamicRendering()) {
			return evaluate();
		}
		
		StringBuilder attrs = new StringBuilder();
		for (Attribute attr : element.getAttributes()) {
			attrs.append(" " + attr);
		}
		
		StringBuilder builder = new StringBuilder("<" + element.getName() + attrs + ">");

		String content = element.getContent().toString();

		Integer counter = 0;
		Integer index = 0;

		while (counter < content.length()) {
			char c = content.charAt(counter);
			if (c != '<') {
				builder.append(c);
				counter++;
			} else {
				ViewElement elt = children.get(index++);
				builder.append(elt);
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
	
	private String evaluate() {
		StringBuilder stream = new StringBuilder();
		String content = element.getContent().toString();

		Integer counter = 0;
		Integer index = 0;

		while (counter < content.length()) {
			char c = content.charAt(counter);
			System.out.println(c);
			
			if (c != '<') {
				if (c == '{') {
					counter +=2;
					StringBuilder expr = new StringBuilder();
					while (counter < content.length() && content.charAt(counter) != '}') {
						expr.append(content.charAt(counter++));
					}
					
					// TODO evaluate expression
					
					stream.append("TODO evaluate this expression:  " + expr);
					
					System.out.println(content.substring(counter));
					if (counter < content.length()) {
						counter += 2;
					}
					System.out.println(content.substring(counter));
				} else {
					stream.append(c);
					counter++;
				}
			} else {
				ViewElement elt = children.get(index++);
				stream.append(elt);
				counter += elt.contentLength();
			}
		}
		
		return "<rendered>"+stream+"</rendered>";
	}

	private String getExpression() {
		String content = element.getContent().toString();
		Integer startIndex = content.indexOf("{{");
		Integer endIndex = content.indexOf("}}");
		
		return content.substring(startIndex+2, endIndex).trim();
	}
	
	private boolean needDyamicRendering() {
		return element.getName().equals("render");
	}

	public Map<String,Object> getGlobalVariables() {
		return globalVariables;
	}

	public void setGlobalVariables(Map<String,Object> globalVariables) {
		this.globalVariables = globalVariables;
	}

	public Map<String,Object> getLocalVaribales() {
		return localVaribales;
	}

	public void setLocalVaribales(Map<String,Object> localVaribales) {
		this.localVaribales = localVaribales;
	}
	
	private Object invoke(String target,String methodname,String ...args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchAttributeException {
		Object[] arguments = new Class[args.length];
		
		for (Integer  index = 0; index < args.length; ++index) {
			Object argument = getVariable(args[index]);
			arguments[index] = argument;
		}
		
		return invoke(target, methodname, arguments);
	}
	
	
	private Object invoke(String target,String methodname, Object ...args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchAttributeException {
		return invoke(getVariable(target),methodname,args);
	}
	
	private Object invoke (Object target, String methodname, Object ...args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?>[] classes = new Class[args.length];
		
		for (Integer  index = 0; index < args.length; ++index) {
			classes[index] = args[index].getClass();
		}
		
		return getMethod(target, methodname, classes).invoke(target, args);
	}
	
	private Object getVariable(String target) throws NoSuchAttributeException {
		if (globalVariables.containsValue(target)) {
			return getGlobalVariable(target);
		} else if (localVaribales.containsKey(target)) {
			return getLocalVariable(target);
		} else {
			throw new NoSuchAttributeException();
		}
	}
	
	private Object getGlobalVariable(String key) {
		return globalVariables.get(key);
	}
	
	private Object getLocalVariable(String key) {
		return localVaribales.get(key);
	}
	
	private Method getMethod(Object target, String methodname,Class<?> ...arguments) throws NoSuchMethodException, SecurityException {
		Method method = target.getClass().getMethod(methodname, arguments);
		method.setAccessible(true);
		return method;
	}
	
	private Object getFieldValue(Object target,String fieldName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = target.getClass().getField(fieldName);
		field.setAccessible(true);
		return field.get(target);
	}
	
	private String stringfyFieldValue(Object target,String fieldName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		return getFieldValue(target, fieldName).toString();	
	}
	
}
