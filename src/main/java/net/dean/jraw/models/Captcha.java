package net.dean.jraw.models;

import java.io.InputStream;

public class Captcha {
	private String id;
	private InputStream imageStream;

	public Captcha(String id, InputStream imageStream) {
		this.id = id;
		this.imageStream = imageStream;
	}

	public String getId() {
		return id;
	}

	public InputStream getImageStream() {
		return imageStream;
	}
}
