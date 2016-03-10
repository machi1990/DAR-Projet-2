package com.upmc.stl.dar.server.configuration.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

public class View {
	private Map<String,Object> globalVariables = new HashMap<>();
	private Map<String,Object> localVaribales = new HashMap<>();
	
	private List<ViewElement> elements = new ArrayList<>();
	
	public View(Source source) {
		super();
		
		for (Element element: source.getChildElements()) {
			elements.add(new ViewElement(element));
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for (ViewElement element: elements) {
			builder.append(element.toString());
		}
		
		return builder.toString();
	}

	public Map<String,Object> getLocalVaribales() {
		return localVaribales;
	}

	public void setLocalVaribales(Map<String,Object> localVaribales) {
		this.localVaribales = localVaribales;
	}

	public Map<String,Object> getGlobalVariables() {
		return globalVariables;
	}

	public void setGlobalVariables(Map<String,Object> globalVariables) {
		this.globalVariables = globalVariables;
	}
}
