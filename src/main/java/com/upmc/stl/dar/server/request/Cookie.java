package com.upmc.stl.dar.server.request;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javafx.util.Pair;

public class Cookie {

	private String path;
	private String domain;
	private Date expires;
	private boolean secure = false;
	private boolean httpOnly = false;
	private Pair<String,String> cookieValue;
	
	/**
	 * A cookie from a pair of key value
	 * @param value
	 */
	public Cookie(Pair<String,String> value) {
		super();
		this.cookieValue = value;
	}

	/**
	 * A cookie from it's string representation
	 * key=value
	 * @param cookie
	 */
	public Cookie(String cookie) throws Error {
		if (cookie == null || !cookie.contains("=")) {
			throw new Error("Invalid cookie");
		}
		
		String pair[] = cookie.split("=");
		
		if (pair.length == 1) {
			throw new Error("Cookie has no value");
		} else if (pair.length == 2) {
			this.cookieValue  = new Pair<String, String>(pair[0], pair[1]);
		} else if (pair.length > 2) {
			this.cookieValue = new Pair<String,String>(pair[0],cookie.substring(cookie.indexOf('=')+1));
		}
	}
	
	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public String getDomain() {
		return domain;
	}


	public void setDomain(String domain) {
		this.domain = domain;
	}


	/**
	 * Returns cookies max age
	 * @return
	 */
	public Date getExpires() {
		return expires;
	}


	/**
	 * Sets cookies max age	passing number of seconds.
	 * @param expires
	 */
	public void setMaxAge(Long seconds) {
		this.expires = Date.from(new Date().toInstant().plusSeconds(seconds));
	}

	public boolean isSecure() {
		return secure;
	}


	public void setSecure(boolean secure) {
		this.secure = secure;
	}


	public boolean isHttpOnly() {
		return httpOnly;
	}


	public void setHttpOnly(boolean httpOnly) {
		this.httpOnly = httpOnly;
	}


	public String getValue() {
		return cookieValue.getValue();
	}

	public String cookieKey() {
		return cookieValue.getKey();
	}
	
	public void setValue(Pair<String,String> value) {
		this.cookieValue = value;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + ((expires == null) ? 0 : expires.hashCode());
		result = prime * result + (httpOnly ? 1231 : 1237);
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + (secure ? 1231 : 1237);
		result = prime * result + ((cookieValue == null) ? 0 : cookieValue.hashCode());
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
		
		Cookie cookie = (Cookie) object;
		if (domain == null) {
			if (cookie.domain != null)
				return false;
		} else if (!domain.equals(cookie.domain))
			return false;
		if (expires == null) {
			if (cookie.expires != null)
				return false;
		} else if (!expires.equals(cookie.expires))
			return false;
		if (httpOnly != cookie.httpOnly)
			return false;
		if (path == null) {
			if (cookie.path != null)
				return false;
		} else if (!path.equals(cookie.path))
			return false;
		if (secure != cookie.secure)
			return false;
		if (cookieValue == null) {
			if (cookie.cookieValue != null)
				return false;
		} else if (!cookieValue.equals(cookie.cookieValue))
			return false;
		return true;
	}



	@Override
	public String toString() {
		String cookie =  "Cookie: "+ cookieValue.getKey() + "="+ cookieValue.getValue();
		
		if (path != null) {
			cookie += "; Path =" + path;
		}
		
		if (expires != null) {
			cookie += "; Expires =" + expires;
		}
		
		if (domain != null) {
			cookie += "; Domain =" + domain;
		}
		
		// TODO rest here
		
		return cookie + "\r\n";
	}
	
	/**
	 * Parse cookies object from it's string cookie's representation.
	 * The result is a map of cookie's key to it's value.
	 * @param cookie
	 * @return
	 */
	
	public static Map<String,Cookie> fromString(String cookie) throws Error {	
		Map<String,Cookie> cookies = new HashMap<>();
		
		String _cookies[] = cookie.split("; ");
		
		Cookie cookie_;
		
		for (String _cookie: _cookies) {
			cookie_ = new Cookie(_cookie);
			cookies.put(cookie_.cookieKey(),cookie_);
		}
		
		return cookies;
	}
}
