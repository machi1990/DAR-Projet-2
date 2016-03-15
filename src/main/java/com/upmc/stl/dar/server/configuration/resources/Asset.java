package com.upmc.stl.dar.server.configuration.resources;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.upmc.stl.dar.server.response.Response;

public class Asset {
	private static Asset welcomeFile = null;
	private Path path;
	private String contentType;
	
	private Asset() {
		super();
	}

	protected Asset(Path path) throws IOException {
		super();
		setAbsPath(path);
	}
	
	protected void setAbsPath(Path path) throws IOException {
		this.path = path;
		contentType = FileContentType.getType(path.toString());
	}
	
	public void sendFile(Response response,OutputStream out) throws IOException {
		response.setContentType(contentType());
		out.write(response.toString().getBytes());
		out.write(Files.readAllBytes(path));
	}
	
	public void makeWelcomeFile() {
		Asset.welcomeFile = clone();
	}
	
	public Asset clone() {
		Asset clone = new Asset();
		
		clone.path = path;
		
		return clone;
	}
	
	public String contentType() {
		return contentType;
	}
	
	@Override
	public String toString() {
		return "Asset [path=" + path +"]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null)
			return false;
		if (getClass() != object.getClass())
			return false;
		
		Asset asset = (Asset) object;
		if (path == null) {
			if (asset.path != null)
				return false;
		} else if (!path.equals(asset.path))
			return false;
		
		return true;
	}
	
	public static boolean hasWelcomeFile() {
		return welcomeFile != null;
	}
	
	public static void makeWelcomeFile(Asset asset) {
		welcomeFile = asset.clone();
	}
	
	public static Asset getWelcomeFile () {
		return welcomeFile;
	}
}
