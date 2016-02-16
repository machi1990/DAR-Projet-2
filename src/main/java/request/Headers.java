package request;

public class Headers {
	private ContentType contentType;
	private String authorization;
	
	public String getAuthorization() {
		return authorization;
	}
	
	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}
	
	public ContentType getContentType() {
		return contentType;
	}
	
	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}
	
	@Override
	public String toString() {
		return "Content-Type: " +this.contentType.name() + '\n' + ", Authorization: " +this.authorization;
	}
}
