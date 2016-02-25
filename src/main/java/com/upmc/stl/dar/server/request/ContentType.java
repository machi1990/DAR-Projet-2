package com.upmc.stl.dar.server.request;

public enum ContentType {
	JSON,
	PLAIN,
	HTML;
	
	@Override
	public String toString() {
		if (this.equals(JSON)) {
			return "application/json";
		}
		return "text/"+ this.name().toLowerCase();
	}
}
