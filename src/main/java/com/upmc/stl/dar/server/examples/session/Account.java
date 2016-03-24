package com.upmc.stl.dar.server.examples.session;

public class Account {
	private static int counter = 0;
	private Integer id;
	private String username;
	private String password;
	private Integer numberOfConnections = 1;
	
	public Account() {
		super();
	}

	public Account(String username, String password) {
		super();
		this.username = username;
		this.password = password;
		this.id = counter++;
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	public Integer getNumberOfConnections() {
		return numberOfConnections;
	}

	public void setNumberOfConnections(Integer numberOfConnections) {
		this.numberOfConnections = numberOfConnections;
	}

	@Override
	public String toString() {
		return "Account [username=" + username + ", password=" + password + ", numberOfConnections="
				+ numberOfConnections + "]";
	}
	
}
