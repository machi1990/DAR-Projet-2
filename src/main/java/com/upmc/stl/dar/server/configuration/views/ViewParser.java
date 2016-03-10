package com.upmc.stl.dar.server.configuration.views;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.htmlparser.jericho.Source;

public class ViewParser {
	private static Map<String,File> files;
	
	static {
		try {
			files = ViewLoader.load();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	
	public static Map<String,View> parses() throws IOException {
		Map<String,View> views = new HashMap<>(); 
		for (String key:files.keySet()) {
			views.put(key, parse(new Source(files.get(key))));
		}
		
		return views;
	}
	
	public static View parse(Source source) {
		return new View(source);
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println(parses());
	}
}
