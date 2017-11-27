package com.upmc.stl.dar.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
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
import com.upmc.stl.dar.server.request.Request;
import com.upmc.stl.dar.server.request.RequestBuilder;
import com.upmc.stl.dar.server.response.Response;
import com.upmc.stl.dar.server.response.Status;
import com.upmc.stl.dar.server.tools.Session;

/**
 * A thread for each connection
 */
public class Connection implements Runnable {
	private Set<Resource> resources = new HashSet<>();
	private Map<String, Asset> assets = new HashMap<>();
	private Map<String, View> views = new HashMap<>();
	private final SocketChannel socketChannel;

	protected Connection(SocketChannel channel) {
		this.socketChannel = channel;
	}

	protected Connection setResources(Set<Resource> resources) {
		synchronized (resources) {
			this.resources = Collections.unmodifiableSet(resources);
		}

		return this;
	}

	protected Connection setAssets(Map<String, Asset> assets) {
		synchronized (assets) {
			this.assets = Collections.unmodifiableMap(assets);
		}

		return this;
	}

	protected Connection setViews(Map<String, View> views) {
		synchronized (views) {
			this.views = Collections.unmodifiableMap(views);
		}

		return this;
	}

	public void run() {
		try {
			Response response = Response.response(Status.OK);
			try {
				Request request = RequestBuilder.newRequest(this.socketChannel);
				if (Request.isForWelcomeFile(request) && Asset.hasWelcomeFile()) {
					sendFile(Asset.getWelcomeFile(), request, response);
				} else if (request.matchesStaticResource()) {
					serveStaticFile(request, response);
				} else {
					response = executeDynamicResource(request);
					sendResponse(response);
				}
			} catch (ServerException | IOException e) {
				response = Response.response(Status.INTERNAL_SERVER_ERROR);
				response.setContentType(ContentType.PLAIN);
				response.build(e.getMessage());
				sendResponse(response);
			} finally {
				this.socketChannel.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Response executeDynamicResource(Request request) throws IOException {
		Response response;
		Object result = invoke(request);
		if (result instanceof Response) {
			response = (Response) result;
		} else {
			response = Response.response(Status.OK);
			response.setContentType(request.getHeaders().contentType());
			response.build(result);
		}

		if (request.hasActiveSession()) { // Update session time
			response.addSession(request.sessionInstance());
		}

		return response;
	}

	private void sendResponse(Response response) throws IOException {
		final byte[] bytes = response.toString().getBytes();
		final ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
		buffer.put(bytes);
		buffer.flip();
		this.socketChannel.write(buffer);
	}

	private void serveStaticFile(Request request, Response response) throws IOException, ServerException {
		final String url = request.getUrl();

		if (!assets.containsKey(url)) {
			throw ExceptionCreator.creator().create(ExceptionKind.NOT_FOUND);
		}

		final Asset asset = assets.get(url);

		sendFile(asset, request, response);
	}

	private void sendFile(final Asset asset, final Request request, Response response) throws IOException {
		if (asset == null) {
			return;
		}

		if (request.hasActiveSession()) { // Update session time
			Session session = request.sessionInstance();
			response.addSession(session);
		}
		response = Response.response(Status.OK);
		response.setContentType(asset.contentType());
		asset.sendFile(response, this.socketChannel);
	}

	private Object invoke(Request request) {
		Object response = Response.response(Status.NOT_IMPLEMENTED);

		for (Resource resource : resources) {
			try {
				response = resource.invoke(request);
				break;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| InstantiationException | IOException e) {
				Response response_ = Response.response(Status.INTERNAL_SERVER_ERROR);
				response_.build(e.getMessage());
				return response_;
			} catch (ServerException notMatchedException) {
				// Catch and continue
			}
		}

		return interceptResponse(response);
	}

	private Object interceptResponse(Object intercepted) {
		if (!(intercepted instanceof Model2View)) {
			return intercepted;
		}

		Model2View model2View = (Model2View) intercepted;

		if (!views.containsKey(model2View.getTemplate())) {
			return Response.response(Status.NOT_FOUND);
		}

		Response response;

		final View view = views.get(model2View.getTemplate());

		try {
			final String content = view.build(model2View.getTemplate(), model2View.getEnvironment());
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
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Connection that = (Connection) o;

		if (resources != null ? !resources.equals(that.resources) : that.resources != null)
			return false;
		if (assets != null ? !assets.equals(that.assets) : that.assets != null)
			return false;
		if (views != null ? !views.equals(that.views) : that.views != null)
			return false;
		return socketChannel != null ? socketChannel.equals(that.socketChannel) : that.socketChannel == null;
	}

	@Override
	public int hashCode() {
		int result = resources != null ? resources.hashCode() : 0;
		result = 31 * result + (assets != null ? assets.hashCode() : 0);
		result = 31 * result + (views != null ? views.hashCode() : 0);
		result = 31 * result + (socketChannel != null ? socketChannel.hashCode() : 0);
		return result;
	}
}
