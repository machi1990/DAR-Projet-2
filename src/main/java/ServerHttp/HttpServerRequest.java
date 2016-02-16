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
	        String stringRequest = "";
            try {
            	s = in.readLine();
            	stringRequest += s + "\n";
            	parser = s.split(" ");
		    	request.setMethod(parser[0]);
		    	//parser[1]; "/index" and parser[2]; "HTTP/1.1"
				while ((s = in.readLine()) != null) {
					
					if (s.isEmpty()) {
		                break;
		            }
				    System.out.println(s);
				    parser = s.split(": ");
				    switch(parser[0]){
				    case "Host" :
				    	request.setHost(parser[1]);
				    	break;
				    case "Connection" :
				    	// Connection: keep-alive
				    	break;
				    case "User-Agent":
				    	// User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36
				    	break;
				    case "Accept":
				    	// Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
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
				    default :
				    }
				}
				
		        out.write("HTTP/1.1 200 OK\r\n");
		        out.write("Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n");
		        out.write("Server: Apache/0.8.4\r\n");
		        out.write("Content-Type: text/html\r\n");
		        out.write("Content-Length: 57\r\n");
		        out.write("Expires: Sat, 01 Jan 2000 00:59:59 GMT\r\n");
		        out.write("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT\r\n");
		        out.write("\r\n");
		        out.write("<TITLE>Exemple</TITLE>");
		        out.write("<P>Ceci est votre requete :</P>");
		        out.write("<H2>"+stringRequest+"</H2>");
		        System.err.println("Connexion avec le client terminée");
		        out.close();
		        in.close();
		        clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}
}