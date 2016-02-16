package ServerHttp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import request.Request;

public class HttpServerRequest {
	public static void main(String[] args){
		int port = 20000;
		ServerSocket serverSocket = null;
        Socket clientSocket = null;
        BufferedReader in = null;
        BufferedWriter out = null;
        Request request = new Request();
		
//		if(args.length < 2){
//			System.out.println("Erreur : nombre d'arguments invalide");
//			return;
//		} else {
//			port = Integer.valueOf(args[1]);
//		}
		
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Erreur : echec du lancement du serveur");
			return;
		}
	    System.out.println("Serveur lancé sur le port : " + port);
	    
	    while (true) {
			try {
				clientSocket = serverSocket.accept();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	        System.err.println("Nouveau client connecté");

	        try {
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
	        
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
		        System.err.println("Connexion avec le client terminée");
		        out.close();
		        in.close();
		        clientSocket.close();
		    	lineNb = 0;
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}
}