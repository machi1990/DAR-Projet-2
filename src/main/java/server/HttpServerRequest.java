package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServerRequest {
	private Integer port;
	private boolean started = false;
	
	private HttpServerRequest() {
		this.port = 20000;
	}
	
	private HttpServerRequest(Integer port) {
		this.port = 20000;
		
		if (port > 10000 && port < 60000) {
			this.port = port;
		}
		
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
		        new Thread( new Server(socket)).start();
			} catch (IOException e1) {
				e1.printStackTrace();
				this.started = false;
				server.close();
			}
		}
	} 
	
	public static void main(String[] args) throws IOException{
		int port = 20000;		
		HttpServerRequest server = new HttpServerRequest(port);
		server.start();
	}
}