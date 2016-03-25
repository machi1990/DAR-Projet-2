package com.upmc.stl.dar.server.tools;

import java.util.Date;

import com.upmc.stl.dar.server.request.Cookie;

import javafx.util.Pair;

public class Session extends Cookie {
	public static final String sessionKey = "____sessionID";
	private Cookie cookie = new Cookie();
	
	private Session() {
		super();
	}
	
	public void clear() {
		cookie.setExpires(new Date());
		cookie.setMaxAge(0l);
	}
	
	public String getValue() {
		return cookie.getValue();
	}

	public String cookieKey() {
		return cookie.cookieKey();
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
	
	/**
	 * TODO intercept requests and get the session id cookie.
	 * @return
	 */
	public final static Session newInstance() {
		Session session = new Session();
		
		session.cookie.setValue(new Pair<String, String>(sessionKey, SessionIdGenerator.generateToken()));
		session.cookie.setExpires(new Long(30*60));
		session.cookie.setMaxAge(new Long(30*60));
		
		return session;
	}

	public static Session newInstance(Cookie cookie) {
		Session session = new Session();
		session.cookie.setValue(cookie.cookieKey(),cookie.getValue());
		session.cookie.setExpires(new Long(60*30));
		session.cookie.setMaxAge(new Long(30*60));
	
		return session;
	}
}
