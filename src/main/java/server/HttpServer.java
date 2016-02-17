package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
	public static final String ServerName = "HomeMade/0.0.1\r\n";
	
	private Integer port;
	private boolean started = false;
	
	private HttpServer() {
		this.port = 20000;
	}

	private HttpServer(Integer port) {
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
				new Thread( new Connection(socket)).start();
			} catch (IOException e1) {
				e1.printStackTrace();
				this.started = false;
				server.close();
			}
		}
	} 

	public static void main(String[] args) throws IOException{
		int port = 20000;		
		HttpServer server = new HttpServer(port);
		server.start();
	}
}