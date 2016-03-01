package com.upmc.stl.dar.server.tools;

import com.upmc.stl.dar.server.request.Cookie;

import javafx.util.Pair;

public class Session extends Cookie {
	private Cookie cookie = new Cookie();
	
	private Session() {
		super();
	}
	
	public String getValue() {
		return cookie.getValue();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cookie == null) ? 0 : cookie.hashCode());
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
		
		Session session = (Session) object;
		
		if (cookie == null) {
			if (session.cookie != null)
				return false;
		} else if (!cookie.equals(session.cookie))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return cookie.toString();
	}
	
	public static Session newInstance() {
		Session session = new Session();

		session.cookie.setValue(new Pair<String, String>("____sessionID", SessionIdGenerator.generateToken()));
		session.cookie.setExpires(new Long(30));
		session.cookie.setMaxAge(new Long(30));
		
		return session;
	}
}
