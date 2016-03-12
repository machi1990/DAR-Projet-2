package com.upmc.stl.dar.server.configuration.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

public class View {
	private List<ViewElement> elements = new ArrayList<>();
	
	protected View(Source source) {
		super();
		
		for (Element element: source.getChildElements()) {
			elements.add(new ViewElement(element));
		}
	}

	public String build(final String template,Map<String,Object> globalVariables) throws Exception {
		StringBuilder builder = new StringBuilder();
		
		for (ViewElement element: elements) {
			builder.append(element.build(template,globalVariables));
		}
		
		return builder.toString();
	}
}
