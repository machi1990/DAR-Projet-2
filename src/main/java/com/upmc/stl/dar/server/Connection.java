package com.upmc.stl.dar.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.upmc.stl.dar.server.configuration.resources.Asset;
import com.upmc.stl.dar.server.configuration.resources.Resource;
import com.upmc.stl.dar.server.configuration.views.Model2View;
import com.upmc.stl.dar.server.configuration.views.View;
import com.upmc.stl.dar.server.exceptions.ExceptionCreator;
import com.upmc.stl.dar.server.exceptions.ExceptionCreator.ExceptionKind;
import com.upmc.stl.dar.server.exceptions.ServerException;
import com.upmc.stl.dar.server.request.ContentType;
import com.upmc.stl.dar.server.request.Headers;
import com.upmc.stl.dar.server.request.Request;
import com.upmc.stl.dar.server.response.Response;
import com.upmc.stl.dar.server.response.Status;
import com.upmc.stl.dar.server.tools.Session;

/**
 * A thread for each connection
 */
public class Connection implements Runnable {
	private Socket socket;
	private Set<Resource> resources = new HashSet<>();
	private Map<String,Asset> assets = new HashMap<>();
	private Map<String, View> views = new HashMap<>();
	
	protected Connection(Socket socket) {
		this.socket = socket;
	}

	protected Connection setResources(Set<Resource> resources) {
		synchronized (resources) {
			this.resources = Collections.unmodifiableSet(resources);
		}
		
		return this;
	}
	
	protected Connection setAssets(Map<String,Asset> assets) {
		synchronized (assets) {
			this.assets = Collections.unmodifiableMap(assets);
		}
		
		return this;
	}
	
	protected  Connection setViews(Map<String, View> views) {
		synchronized (views) {
			this.views = Collections.unmodifiableMap(views);
		}
		
		return this;
	}

	public void run() {
		OutputStream writer = null;

		try {
			socket.setSoTimeout(1);
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = socket.getOutputStream();

			Request request = new Request();
			Response response = Response.response(Status.OK);
			String stringRequest = readInput(reader);

			try {
				afterInputRetrieved(stringRequest, request);
				Object result;
				
				if (Request.isForWelcomeFile(request) && Asset.hasWelcomeFile()) {
					sendFile(Asset.getWelcomeFile(),response,socket.getOutputStream());
				} else if (request.matchesStaticResource()) {
					serveStaticFile(request,response,socket.getOutputStream());
				} else {
					 result = serve(request);
					 if (result instanceof Response) {
							response = (Response) result;
						} else {
							response = Response.response(Status.OK);
							response.setContentType(request.getHeaders().contentType());
							response.build(result);
						}	
				}
			} catch (ServerException e) {
				response = Response.response(Status.INTERNAL_SERVER_ERROR);
				response.setContentType(ContentType.PLAIN);
				response.build(e.getMessage());
			}
			
			if (request.hasActiveSession()) { // Update session time
				Session session = request.newSessionInstance();	
				response.addSession(session);
			}
			
			writer.write(response.toString().getBytes());
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

	private void afterInputRetrieved(String input, Request request) throws ServerException  {
		String[] inputs = input.split("\r\n");
		Headers headers = new Headers();

		if (input.isEmpty()) {
			throw ExceptionCreator.creator().create(ExceptionKind.BAD_INPUT);
		}

		String[] methodUrlContainer = inputs[0].split(" ");
		request.setMethod(methodUrlContainer[0]);
		request.setUrl(methodUrlContainer[1]);

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

	private Object serve(Request request) {	
		return invoke(request);
	}

	private void serveStaticFile(Request request,Response response, OutputStream out) throws IOException, ServerException {
		String url = request.getUrl();
		
		if (!assets.containsKey(url)) {
			throw ExceptionCreator.creator().create(ExceptionKind.NOT_FOUND);
		}
		
		Asset asset = assets.get(url);	
		sendFile(asset,response,out);	
	}
	
	private void sendFile (Asset asset,Response response2, OutputStream stream) throws IOException {
		Response response;
		
		if (asset == null) {
			return;
		}
	
		response = Response.response(Status.OK);
		response.setContentType(asset.contentType());
		asset.sendFile(response, stream);
		
	}
	
	private Object invoke(Request request) {
		Object response = Response.response(Status.NOT_IMPLEMENTED);
		
		for (Resource resource: resources) {
			try {
				response = resource.invoke(request);
				break;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException | IOException e) {
				Response response_ = Response.response(Status.INTERNAL_SERVER_ERROR);
				response_.build(e.getMessage());
				return response_;
			} catch (ServerException notMatchedException) {
				// Catch and continue
				
			}
		}
		
		return interceptResponse(response);
	}
	
	private Object interceptResponse (Object intercepted) {
		if (! (intercepted instanceof Model2View)) {
			return intercepted;
		}
		
		Model2View model2View = (Model2View)intercepted;
		
		if (!views.containsKey(model2View.getTemplate())) {
			return Response.response(Status.NOT_FOUND);
		}
		
		Response response;
		
		View view = views.get(model2View.getTemplate());
		
		try {
			String content = view.build(model2View.getTemplate(), model2View.getEnvironment());
			response = Response.response(Status.OK);
			response.setContentType(ContentType.HTML);
			response.build(content);
		} catch (Exception e) {
			response = Response.response(Status.INTERNAL_SERVER_ERROR);
			response.setContentType(ContentType.HTML);
			response.build(e.getMessage());
		}
		
		return response;
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
