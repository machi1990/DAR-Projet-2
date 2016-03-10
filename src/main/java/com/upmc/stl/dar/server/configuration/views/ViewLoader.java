package com.upmc.stl.dar.server.configuration.views;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class ViewLoader {
	protected static final Map<String,File> load() throws Exception {
		ClassLoader loader = ViewLoader.class.getClassLoader();

		URL resource =  loader.getResource("views");
		
		if (resource == null) {
			/**
			 * Send an error message
			 */
			throw new Error();
		}
		
		File directory = new File(resource.toURI());
		
		return retrieveViewsFrom(directory.getParent().replace("\\", "/"), directory);
	}
	
	private static final Map<String,File> retrieveViewsFrom(String base,File directory) {
		Map<String,File> views = new HashMap<>();
		File[] files = directory.listFiles();
		String name;
		  
		  for (File file : files) {
		        if (file.isFile()) {
		        	name = file.getAbsolutePath().replace("\\","/").split(base)[1];
		        	views.put(name, file);
		        } else if (file.isDirectory()) {
		            views.putAll(retrieveViewsFrom(base, file));
		        }
		    }
		  
		return views;
	}
}
