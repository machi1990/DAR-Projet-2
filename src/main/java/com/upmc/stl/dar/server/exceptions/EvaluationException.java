package com.upmc.stl.dar.server.exceptions;

public class EvaluationException extends ServerException {
	private static final long serialVersionUID = -4728989094132878101L;
	private String template;
	private String expresion;
		
	public EvaluationException(String template, String expresion) {
		super();
		this.template = template;
		this.expresion = expresion;
	}

	@Override
	public String getMessage() {
		return expresion + " inside template "+template+" does not match provided syntax.";
	}

}
