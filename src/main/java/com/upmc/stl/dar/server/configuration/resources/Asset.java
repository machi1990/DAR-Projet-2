package com.upmc.stl.dar.server.configuration.resources;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import com.upmc.stl.dar.server.response.Response;

public class Asset implements Cloneable {
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

	public void sendFile(Response response,SocketChannel channel) throws IOException {
		response.setContentType(contentType());
		byte[] bytes = response.toString().getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
		buffer.put(bytes);
		buffer.flip();
		channel.write(buffer);
		bytes = Files.readAllBytes(path);
		buffer = ByteBuffer.allocate(bytes.length);
		buffer.put(bytes);
		buffer.flip();
		channel.write(buffer);
	}

	public void makeWelcomeFile() throws CloneNotSupportedException {
		Asset.welcomeFile = clone();
	}
	
	public Asset clone() throws CloneNotSupportedException {
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
		try {
			welcomeFile = asset.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	public static Asset getWelcomeFile () {
		return welcomeFile;
	}
}
