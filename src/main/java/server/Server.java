package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import request.ContentType;
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
				}
				
				Headers headers = new Headers();
				headers.setAuthorization("");
				headers.setContentType(ContentType.PLAIN);
				request.setHeaders(headers);
				
				//parser[1]; "/index" and parser[2]; "HTTP/1.1"
				while ((s = in.readLine()) != null) {
					stringRequest += s + "\n";
					if (s.isEmpty()) {
						break;
					}
					parser = s.split(": ");
					switch(parser[0]){
					case "Host" :
						request.setHost(parser[1]);
						break;
					case "Connection" :
						// Connection: keep-alive
						request.setBody(parser[1]);
						break;
					case "User-Agent":
						// User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36
						break;
					case "Content-Type":
						if (parser[1].contains("html")) {
							headers.setContentType(ContentType.HTML);
						} else if (parser[1].contains("json")) {
							headers.setContentType(ContentType.JSON);
						}
						break;
					case "Upgrade-Insecure-Requests":
						// Upgrade-Insecure-Requests: 1
						break;
					case "Accept-Encoding":
						// Accept-Encoding: gzip, deflate, sdch
						break;
					case "Accept-Language":
						// Accept-Language: fr-FR,fr;q=0.8,en-US;q=0.6,en;q=0.4
						break;
					case "Referer":
						//Referer: http://localhost:20000/
						break;
					case "Accept":
						// Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
						break;
					case "Authorization":
						headers.setAuthorization(parser[1]);
						break;	
					default :
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

}
