package com.upmc.stl.dar.server.examples.session;

public class Account {
	private static int id = 0;
	private String username;
	private String password;
	
	public Account(String username, String password) {
		super();
		this.username = username;
		this.password = password;
		this.id = id++;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
