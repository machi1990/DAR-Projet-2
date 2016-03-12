package com.upmc.stl.dar.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.upmc.stl.dar.server.configuration.resources.Asset;
import com.upmc.stl.dar.server.configuration.resources.Resource;
import com.upmc.stl.dar.server.configuration.views.View;

public class Dispatcher {
	private static Dispatcher dispatcher = new Dispatcher();
	private Set<Resource> resources = new HashSet<>();
	private Map<String,Asset> assets = new HashMap<>();
	
	private final ExecutorService connectionPool;
	private Map<String, View> views = new HashMap<>();
	
	private Dispatcher() {
		super();
		connectionPool = Executors.newCachedThreadPool();
	}
	
	protected final void dispatch(Connection newConnection) {
		connectionPool.execute(newConnection.setResources(resources).setAssets(assets).setViews(views));
	}
	
	protected final void setResources(final Set<Resource> resources) {
		this.resources = resources;
	}
	
	protected final void setAssets(final Map<String,Asset> assets) {
		this.assets = assets;
	}
	
	protected final void setViews(final Map<String,View> views) {
		this.views  = views;
	}
	
	protected final static Dispatcher dispatcher() {
		return dispatcher;
	}
}
