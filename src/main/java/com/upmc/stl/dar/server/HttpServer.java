package com.upmc.stl.dar.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.upmc.stl.dar.server.configuration.resources.ResourceConfig;
import com.upmc.stl.dar.server.configuration.views.ViewParser;

public class HttpServer {
	private static final String SEPARATOR = "\r\n";
	public static final String SERVER_NAME = "HomeMade/0.0.1";
	
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
	
	public final void retrieveResources(ResourceConfig config) throws Exception {
		dispatcher.setResources(config.getResources());
		dispatcher.setAssets(config.getAssets());
		dispatcher.setViews(ViewParser.parses());
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

	public static final String separtor() {
		return SEPARATOR;
	}
	
	public static final void start(int port,ResourceConfig config) throws Exception {
		if (config == null) {
			throw new IllegalArgumentException("Can not start server with non registered resources");
		}
		
		HttpServer server = new HttpServer(port);
		server.retrieveResources(config);
		server.start();
	}
	
	public static void main(String[] args) throws Exception {
		/**
		 * TODO get port argument from args
		 */
		
		/**
		 * Registration of all samples applications using their package name.
		 */
		ResourceConfig config = ResourceConfig.newConfiguration();
		config.packages("com.upmc.stl.dar.server.examples");
		
		int port = 30001;
		start(port,config);
	}
}