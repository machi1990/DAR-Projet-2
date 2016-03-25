package com.upmc.stl.dar.server.examples.session;

import java.util.Date;
import java.util.HashMap;

import com.upmc.stl.dar.server.annotation.CONSUMES;
import com.upmc.stl.dar.server.annotation.CONSUMES.Consumed;
import com.upmc.stl.dar.server.annotation.GET;
import com.upmc.stl.dar.server.annotation.PATH;
import com.upmc.stl.dar.server.annotation.POST;
import com.upmc.stl.dar.server.configuration.views.Model2View;
import com.upmc.stl.dar.server.request.Request;
import com.upmc.stl.dar.server.request.UrlParameters;
import com.upmc.stl.dar.server.response.Response;
import com.upmc.stl.dar.server.response.Status;
import com.upmc.stl.dar.server.tools.Session;

@PATH("/session")
public class SessionApplication {
	public static HashMap<Account, String> database = new HashMap<>();
	public static HashMap<String, Account> activeSession = new HashMap<>();

	@POST
	@PATH("/subscribe")
	public Response postSubscribe(Request request, UrlParameters params) {
		Response response;
		Account account = new Account(params.get("username"), params.get("password"));
		if (!database.keySet().contains(account)) {
			request.clearSession();
			response = Response.response(Status.OK);
			Session session = Session.newInstance();
			response.addSession(session);
			database.put(account, session.getValue());
			activeSession.put(session.getValue(), account);
		} else {
			response = Response.response(Status.UNAUTHORIZED);
			response.build("Username already exists. Choose a different one.");
		}

		return response;
	}

	@POST
	@CONSUMES(Consumed.JSON)
	@PATH("/connect")
	public Response getConnect(Request request, Account acc) {
		Response response;
		if (!database.containsKey(acc)) {
			response = Response.response(Status.UNAUTHORIZED);
			response.build("build error message : Username not found");
			return response;
		}

		Account account = null;
		for (Account accnt : database.keySet()) {
			if (accnt.equals(acc)) {
				account = accnt;
				break;
			}
		}

		if (!account.getPassword().equals(acc.getPassword())) {
			response = Response.response(Status.UNAUTHORIZED);
			response.build("build error message : wrong password");
		} else {
			response = addToActiveSession(request, account);
		}

		return response;
	}

	private Response addToActiveSession(Request request, Account account) {
		Response response = Response.response(Status.OK);
		account.setNumberOfConnections(account.getNumberOfConnections() + 1);
		if (!request.hasActiveSession()) {
			Session session = Session.newInstance();
			database.replace(account, session.getValue());
			activeSession.put(session.getValue(), account);
			response.addSession(session);
		} else {
			activeSession.put(request.sessionInstance().getValue(), account);
		}
		
		return response;
	}

	@GET
	@PATH("/disconnect")
	public Response getDisconnect(Request request) {	
		Response response = Response.redirect("/session/subscribe.html");
		clearSession(request, response);
		return response;
	}

	@GET
	@PATH("/index")
	public Object getIndex(Request request) {
		if (!request.hasActiveSession() || !activeSession.containsKey(request.sessionInstance().getValue())) {
			Response response = Response.redirect("/session/subscribe.html");
			clearSession(request, response);
			return response;
		} else {
			Model2View model2Vie = new Model2View("/views/session/index.html");
			Account account = activeSession.get(request.sessionInstance().getValue());
			model2Vie.put("session_id", request.sessionInstance().getValue());
			model2Vie.put("connections", account.getNumberOfConnections());
			model2Vie.put("name", account.getUsername());
			model2Vie.put("password", account.getPassword());
			model2Vie.put("date", new Date().toString());
			return model2Vie;
		}
	}

	private void clearSession(Request request, Response response) {
		if (request.hasActiveSession()) {
			activeSession.remove(request.sessionInstance().getValue());
			Session session = request.sessionInstance();
			session.clear();
			response.addSession(session);
			request.clearSession();
		}
	}
}
