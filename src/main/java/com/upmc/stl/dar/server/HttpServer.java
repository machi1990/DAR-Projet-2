package com.upmc.stl.dar.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.upmc.stl.dar.server.configuration.resources.ResourceConfig;
import com.upmc.stl.dar.server.configuration.views.ViewParser;

public class HttpServer {
	private static final String SEPARATOR = "\r\n";
	public static final String SERVER_NAME = "HomeMade/1.0.0";
	
	private Integer port = 20000;
	private Boolean started = false;
	private Dispatcher dispatcher = Dispatcher.dispatcher();

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


		ServerSocketChannel  channel = ServerSocketChannel.open();
		channel.socket().bind(new InetSocketAddress(port));
		channel.configureBlocking(false);
		System.out.println("Server started at "+ port);
		
		this.started = true;
		while (true) {
			try {
			    final SocketChannel socketChannel = channel.accept();
			    if (socketChannel == null) continue;

				dispatcher.dispatch(new Connection(socketChannel));
			} catch (IOException e1) {
				e1.printStackTrace();
				this.started = false;
				channel.close();
			}
		}
	} 

	public static final String separator() {
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
		
		int port = 30000;
		start(port,config);
	}
}