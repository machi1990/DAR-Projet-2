package com.upmc.stl.dar.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.upmc.stl.dar.server.exceptions.ServerException;
import com.upmc.stl.dar.server.resource.configuration.ResourceConfig;

public class HttpServer {
	public static final String SERVER_NAME = "HomeMade/0.0.1\r\n";
	private Integer port;
	private Boolean started = false;
	private Dispatcher dispatcher = Dispatcher.dispatcher();
	
	private HttpServer() {
		this.port = 20000;
	}

	private HttpServer(Integer port) {
		this.port = 20000;

		if (port > 10000 && port < 60000) {
			this.port = port;
		}
	}
	
	public final void retrieveResources(ResourceConfig config) throws ServerException, IOException {
		dispatcher.setResources(config.getResources());
		dispatcher.setAssets(config.getAssets());
	}

	public final void start() throws IOException {
		if (started) {
			System.err.println("Server already started");
			return;
		}

		Socket socket;
		ServerSocket server = new ServerSocket(port);

		System.out.println("Server started at "+ port);
		
		this.started = true;
		while (true) {
			try {
				socket = server.accept();
				System.err.println("New client connected");
				dispatcher.dispatch(new Connection(socket));
			} catch (IOException e1) {
				e1.printStackTrace();
				this.started = false;
				server.close();
			}
		}
	} 

	public static final void start(int port,ResourceConfig config) throws IOException, IllegalArgumentException, ServerException {
		if (config == null) {
			throw new IllegalArgumentException("Can not start server with non registered resources");
		}
		
		HttpServer server = new HttpServer(port);
		server.retrieveResources(config);
		server.start();
	}
	
	public static void main(String[] args) throws IOException, IllegalArgumentException, ServerException {
		/**
		 * TODO get port argument from args
		 */
		
		/**
		 * Registration of all samples applications using their package name.
		 */
		ResourceConfig config = ResourceConfig.newConfiguration();
		config.packages("com.upmc.stl.dar.server.examples");
		
		int port = 30000;
		start(port,config);
	}
}