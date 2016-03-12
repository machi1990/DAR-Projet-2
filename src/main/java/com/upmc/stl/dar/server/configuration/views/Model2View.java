package com.upmc.stl.dar.server.configuration.views;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Model2View {
	private String template;
	private Map<String,Object> environment = new HashMap<>();
	
	public Model2View(String template) {
		super();
		this.template = template;
	}
	
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	
	public Map<String,Object> getEnvironment() {
		return environment;
	}
	
	public Model2View put(String key,Object value) {
		environment.put(key.trim(), value);
		return this;
	}
	
	/**
	 * Trim key
	 * @param values
	 * @return
	 */
	public Model2View putAll(Map<String,Object> values) {
		for (String key:values.keySet()) {
			put(key, values.get(key));
		}
		return this;
	}
	
	public int size() {
		return environment.size();
	}
	
	public boolean isEmpty() {
		return environment.isEmpty();
	}
	
	public boolean containsKey(Object key) {
		return environment.containsKey(key);
	}
	
	public boolean containsValue(Object value) {
		return environment.containsValue(value);
	}
	
	public Object remove(Object key) {
		return environment.remove(key);
	}
	
	public void clear() {
		environment.clear();
	}
	
	public Set<String> keySet() {
		return environment.keySet();
	}
	
	public Collection<Object> values() {
		return environment.values();
	}
	
	public boolean remove(Object key, Object value) {
		return environment.remove(key, value);
	}
	
	public boolean replace(String key, Object oldValue, Object newValue) {
		return environment.replace(key, oldValue, newValue);
	}
	
	public Object replace(String key, Object value) {
		return environment.replace(key, value);
	}
}
