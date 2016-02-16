package ServerHttp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import request.Request;

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
		        try {
		        	int lineNb = 0;
					while ((s = in.readLine()) != null) {
						
						if (s.isEmpty()) {
			                break;
			            }
						lineNb++;
					    System.out.println(s);
					    parser = s.split(" ");
					    switch(lineNb){
					    case 1 :
					    	request.setMethod(parser[0]);
					    	//parser[1]; "/index" and parser[2]; "HTTP/1.1"
					    	break;
					    case 2:
					    	request.setHost(parser[1]);
					    	break;
					    case 3:
					    	// Connection: keep-alive
					    	break;
					    case 4:
					    	// Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
					    	break;
					    case 5:
					    	// Upgrade-Insecure-Requests: 1
					    	break;
					    case 6:
					    	// User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36
					    	break;
					    case 7:
					    	// Accept-Encoding: gzip, deflate, sdch
					    	break;
					    case 8:
					    	// Accept-Language: fr-FR,fr;q=0.8,en-US;q=0.6,en;q=0.4
					    	break;
					    case 9:
					    	break;
					    default :
					    }
					}
			        System.err.println("Client connexion closed");
			        out.close();
			        in.close();
			        socket.close();
			    	lineNb = 0;
				} catch (IOException e) {
					e.printStackTrace();
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
        
       
	}

}
