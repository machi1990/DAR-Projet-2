package com.upmc.stl.dar.server.examples.demo;

import java.util.Date;

public class Message {
	private String postedBy;
	private Date postedAt;
	private String content;
	
	public Message(String postedBy, Date postedAt, String content) {
		super();
		this.postedBy = postedBy;
		this.postedAt = postedAt;
		this.content = content;
	}
	
	public Message() {
		super();
	}

	public String getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(String postedBy) {
		this.postedBy = postedBy;
	}

	public Date getPostedAt() {
		return postedAt;
	}

	public void setPostedAt(Date postedAt) {
		this.postedAt = postedAt;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
