package com.upmc.stl.dar.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

import com.upmc.stl.dar.server.resource.configuration.ParamConflictException;
import com.upmc.stl.dar.server.resource.configuration.Resource;
import com.upmc.stl.dar.server.resource.configuration.ResourceConfig;
import com.upmc.stl.dar.server.resource.configuration.ResourceParam.NotSupportedException;
import com.upmc.stl.dar.server.resource.configuration.ResourcesNotFoundException;

public class HttpServer {
	public static final String ServerName = "HomeMade/0.0.1\r\n";
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
	
	public void setResources(Set<Resource> resources) {
		dispatcher.setResources(resources);
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

	public static void start(int port,ResourceConfig config) throws IOException, IllegalArgumentException, ResourcesNotFoundException, NotSupportedException, ParamConflictException{
		
		if (config == null) {
			throw new IllegalArgumentException("Can not start server with non registered resources");
		}
		
		/**
		 * ResourceConfig config = new ResourceConfig();
		 * 
		 * config.packages("application");
		 *
		 *	config.classes(Application.class);
		 */
				
		HttpServer server = new HttpServer(port);
		server.setResources(config.getResources());
		server.start();
	}
	
	public static void main(String[] args) throws IOException, IllegalArgumentException, ResourcesNotFoundException, NotSupportedException, ParamConflictException{
		/**
		 * TODO get port argument from args
		 */
		
		/**
		 * Echo application registration. The application class resides inside the application package
		 */
		ResourceConfig config = new ResourceConfig();
		config.packages("com.upmc.stl.dar.server.examples");
		
		int port = 30000;
		start(port,config);
	}
}