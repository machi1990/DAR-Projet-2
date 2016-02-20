package server.annotation;

public @interface CONSUMES {
	Consumed value();
	
	public static enum Consumed {
		STRING,
		JSON
	}
}
