package server.configuration;

public class NotMatchedException extends Exception{
	private static final long serialVersionUID = 7294454445986653792L;

	@Override
	public String getMessage() {
		return "Not matched";
	}
}
