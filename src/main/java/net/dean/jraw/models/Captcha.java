package net.dean.jraw.models;

import java.io.InputStream;

/**
 * Represents a Captcha.
 */
public class Captcha {
	/**
	 * The ID of the captcha
	 */
	private String id;

	/**
	 * The InputStream that will download the Captcha's image
	 */
	private InputStream imageStream;

	/**
	 * Instantiates a new Captcha
	 *
	 * @param id          The captcha's ID
	 * @param imageStream The captcha's input stream that will download its image
	 */
	public Captcha(String id, InputStream imageStream) {
		this.id = id;
		this.imageStream = imageStream;
	}

	/**
	 * Gets the captcha's ID
	 *
	 * @return The captcha's ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the captcha's InputStream that will download an image
	 *
	 * @return The captcha's InputStream
	 */
	public InputStream getImageStream() {
		return imageStream;
	}
}
