package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import request.Headers;
import request.Request;
import response.Response;
import response.Status;

/**
 * A thread for each connection
 */
public class Server implements Runnable {
	private Socket socket;

	public Server(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		Request request = new Request();
		BufferedReader in = null;
		BufferedWriter out = null;

		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			String s;
			String parser[];
			String stringRequest = "";
			try {
				if(((s = in.readLine()) != null)) {
					stringRequest += s + "\n";
					parser = s.split(" ");
					request.setMethod(parser[0]);
				} else {
					return;
				}
				
				Headers headers = new Headers();
				request.setHeaders(headers);
				
				//parser[1]; "/index" and parser[2]; "HTTP/1.1"
				while ((s = in.readLine()) != null) {
					stringRequest += s + "\n";
					if (s.isEmpty()) {
						break;
					}
					
					parser = s.split(": ");
					switch(parser[0]){
					case "Body":
						System.out.println(s);
						break;
					default :
						headers.put(parser[0], parser[1]);
						break;
					}
				}

				Response response = Response.response(Status.UNAUTHORIZED);				
				response.build(request);
				
				out.write(response.toString());
				System.out.println(stringRequest);
				System.err.println("Client connexion closed");
				out.close();
				in.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((socket == null) ? 0 : socket.hashCode());
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
		Server server = (Server) object;
		if (socket == null) {
			if (server.socket != null)
				return false;
		} else if (!socket.equals(server.socket))
			return false;
		return true;
	}

}
