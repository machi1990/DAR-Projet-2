package server;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ResourceConfig {

	private Map<String, Resource<Object>> configuration = new HashMap<>();
	
	public ResourceConfig() {
		super();
	}
	
	public Map<String, Resource<Object>> getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Map<String, Resource<Object>> configuration) {
		this.configuration = configuration;
	}

	/**
	 * register all resources located  under the given packages name
	 * @param packes
	 */
	public void packages(String ...packages) {
		// TODO using class loader to all all class and create an instance
	}
	
	public static class Resource<Clazz> {
		public Class<Clazz> clazz;
		public Method method;
	}
}
