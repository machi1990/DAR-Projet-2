package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

import exceptions.BadInputException;
import request.ContentType;
import request.Headers;
import request.Request;
import response.Response;
import response.Status;

/**
 * A thread for each connection
 */
public class Connection implements Runnable {
	private Socket socket;

	public Connection(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		BufferedWriter writer = null;
		
		try {
			socket.setSoTimeout(1);
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			Request request = new Request();
			Response response;
			String stringRequest = readInput(reader);
			
			try {
				afterInputRetrieved(stringRequest, request);
				
				response = Response.response(Status.OK);
				response.setContentType(request.getHeaders().contentType());
				response.build(request);
			} catch (BadInputException e) {
				response = Response.response(Status.NOT_FOUND);
				response.setContentType(ContentType.PLAIN);
				response.build("Null request");
			}

			writer.write(response.toString());
			System.err.println("Client connexion closed");

			writer.flush();
			writer.close();
			reader.close();
			socket.close();

		} catch (IOException e) {
			System.err.println("Error in Connection.run()");
		}
	}

	private String readInput(BufferedReader reader) throws IOException {
		StringBuilder builder = new StringBuilder();
		char[] charBuffer = new char[128];
		int bytesRead = -1;

		try {
			while ((bytesRead = reader.read(charBuffer)) > 0) {
				builder.append(charBuffer, 0, bytesRead);
			}
		} catch (SocketTimeoutException timeout) {
		}

		return builder.toString();
	}

	private void afterInputRetrieved(String input, Request request) throws BadInputException {
		String[] inputs = input.split("\r\n");
		Headers headers = new Headers();

		if(input.isEmpty()){
			throw new BadInputException();
		}

		String[] methodUrlContainer = inputs[0].split(" ");
		request.setMethod(methodUrlContainer[0]);
		request.setResourceUrl(methodUrlContainer[1]);
		
		for (int i = 1; i < inputs.length - 1; ++i) {
			parse(inputs[i], request, headers);
		}

		switch (request.getMethod()) {
		case POST:
		case PUT:
			request.setBody(inputs[inputs.length - 1]);
			break;
		default:
			parse(inputs[inputs.length - 1], request, headers);
			break;
		}

		request.setHeaders(headers);
	}

	private void parse(String value, Request request, Headers headers) {
		if (value == null || value.isEmpty()) {
			return;
		}

		int index = value.indexOf(":");
		if (index < 0) {
			return;
		}

		String key = value.substring(0, index);
		value = value.substring(index + 1).trim();

		switch (key) {
		case "Cookie":
			request.setCookies(value);
			break;
		default:
			headers.put(key, value);
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
		Connection connection = (Connection) object;
		if (socket == null) {
			if (connection.socket != null)
				return false;
		} else if (!socket.equals(connection.socket))
			return false;
		return true;
	}

}
