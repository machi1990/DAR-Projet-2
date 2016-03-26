package com.upmc.stl.dar.server.examples.demo;

import com.upmc.stl.dar.server.examples.session.Account;

public class User extends Account{
	String fullName;
	String image;
	
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
