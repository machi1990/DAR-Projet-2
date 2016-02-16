package ServerHttp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServerRequest {
	public static void main(String[] args){
		int port = 20000;
		ServerSocket serverSocket = null;
        Socket clientSocket = null;
        BufferedReader in = null;
        BufferedWriter out = null;
		
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
            try {
				while ((s = in.readLine()) != null) {
				    System.out.println(s);
				    
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}
}