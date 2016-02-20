package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import application.Application;
import server.configuration.ResourceConfig;
import server.configuration.ResourcesNotFoundException;

public class HttpServer {
	public static final String ServerName = "HomeMade/0.0.1\r\n";
	private Integer port;
	private boolean started = false;
	private Set<Class<?>> resources = new HashSet<>();
	
	private HttpServer() {
		this.port = 20000;
	}

	private HttpServer(Integer port) {
		this.port = 20000;

		if (port > 10000 && port < 60000) {
			this.port = port;
		}

	}

	public Set<Class<?>> getResources() {
		return resources;
	}

	public void setResources(Set<Class<?>> resources) {
		this.resources = resources;
	}
	
	public void start() throws IOException {
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
				new Thread( new Connection(socket)).start();
			} catch (IOException e1) {
				e1.printStackTrace();
				this.started = false;
				server.close();
			}
		}
	} 

	public static void start(int port,ResourceConfig config) throws IOException, IllegalArgumentException, ResourcesNotFoundException{
		
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
		server.setResources(config.getClasses());
		server.start();
	}
	
	public static void main(String[] args) throws IOException, IllegalArgumentException, ResourcesNotFoundException{
		/**
		 * TODO get port argument from args
		 */
		
		/**
		 * Echo application registration. The application class resides inside the application package
		 */
		ResourceConfig config = new ResourceConfig();
		config.packages("application");
		
		int port = 20000;
		start(port,config);
	}
}