package com.upmc.stl.dar.server;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.upmc.stl.dar.server.resource.configuration.Resource;

public class Dispatcher {
	private static Dispatcher dispatcher = new Dispatcher();
	private Set<Resource> resources = new HashSet<>();
	private final ExecutorService connectionPool;
	
	private Dispatcher() {
		super();
		connectionPool = Executors.newCachedThreadPool();
	}
	
	protected void dispatch(Connection newConnection) {
		connectionPool.execute(newConnection.setResources(resources));
	}
	
	protected void setResources(Set<Resource> resources) {
		this.resources = resources;
	}
		
	protected static Dispatcher dispatcher() {
		return dispatcher;
	}
}
