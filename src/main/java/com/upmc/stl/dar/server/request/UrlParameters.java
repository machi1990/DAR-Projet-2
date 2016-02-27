package com.upmc.stl.dar.server.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UrlParameters {
	private Map<String,String> values = new HashMap<>();
	
	private UrlParameters() {
		super();
	}
	
	public Map<String,String> getValues() {
		return Collections.unmodifiableMap(values);
	}
	
	public void put(String key, String value) {
		values.put(key, value);
	}
	
	public String get(String key) {
		return values.get(key);
	}
	
	@Override
	public String toString() {
		return "UrlParameters [values=" + values + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((values == null) ? 0 : values.hashCode());
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
		UrlParameters other = (UrlParameters) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

	public static UrlParameters putParamsTo(UrlParameters urlParams,String paramaters) {
		String params[] = paramaters.trim().split("&");
		
		Integer index = 0;
		
		for (String param:params) {
			index  = param.indexOf("=");
			if (index == -1) {
				continue;
			}
			
			urlParams.put(param.substring(0,index), param.substring(index+1));
		}
		
		
		return urlParams;
	}

	public static UrlParameters newInstance() {
		return new UrlParameters();
	}
}
