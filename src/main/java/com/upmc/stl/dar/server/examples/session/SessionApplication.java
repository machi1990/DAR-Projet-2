package com.upmc.stl.dar.server.examples.session;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.upmc.stl.dar.server.annotation.GET;
import com.upmc.stl.dar.server.annotation.PATH;
import com.upmc.stl.dar.server.annotation.POST;
import com.upmc.stl.dar.server.configuration.views.Model2View;
import com.upmc.stl.dar.server.request.ContentType;
import com.upmc.stl.dar.server.request.Request;
import com.upmc.stl.dar.server.request.UrlParameters;
import com.upmc.stl.dar.server.response.Response;
import com.upmc.stl.dar.server.response.Status;

@PATH("/session")
public class SessionApplication {

	private ArrayList<Account> accounts = new ArrayList<Account>();
	
	public HashMap<String,Integer> connectedId = new HashMap<>();
	
	@GET
	@PATH("/subscribe")
	public Response getSubscribe(){
		Response response = Response.response(Status.OK);
		response.setContentType(ContentType.HTML);
		
		response.build("find subscribe.html");
		
		return response;
	}
	
	@GET
	@PATH("/subscribe/test")
	public Model2View getSubscribeTest(){
		Model2View view = new Model2View("/views/subscribe.html");
		ArrayList<String> values = new ArrayList<>();
		values.add("Mambooooo");
		values.add("POAAA");
		values.add("SHWAAARI");
		view.put("origin",values);
		return view;
	}
	
	@POST
	@PATH("/subscribe")
	public void postSubscribe(UrlParameters params){
		Account account = new Account(params.get("username"),params.get("password"));
		accounts.add(account);
	}
	
	@GET
	@PATH("/connect")
	public Response getConnect(Request request, UrlParameters params){
		Response response = Response.response(Status.OK);
		response.setContentType(ContentType.HTML);
		Account account = null;
		for(Account a : accounts){
			if(a.getUsername() == params.get("username")){
				account = a;
			}
		}
		if(account == null){
			response.build("build error message : Username not found");
		} else {
			if(account.getPassword() != params.get("password")){
				response.build("build error message : wrong password");
			} else {
				response.build("send index page");
				connectedId.put(request.sessionInstance().getValue(), account.getId());
			}
		}
		
		return response;
	}
	
	@GET
	@PATH("/disconnect")
	public Response getDisconnect(Request request){
		Response response = Response.response(Status.OK);
		response.setContentType(ContentType.HTML);
		response.build("");
		
		if (request.hasActiveSession()) {
			connectedId.remove(request.sessionInstance().getValue());
		}
		
		
		return response;
	}
	
	@GET
	@PATH("/index")
	public Response getIndex(Request request){
		Response response = Response.response(Status.OK);
		response.setContentType(ContentType.HTML);
		response.build("");
		
		if(!request.hasActiveSession()){
			// Connectez vous !
		} else if (connectedId.get(request.sessionInstance()) == null){
			
		} else {
			accounts.get(connectedId.get(request.sessionInstance()));
		}
		
		return response;
	}
}
