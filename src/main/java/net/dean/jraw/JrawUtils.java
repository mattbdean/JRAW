package net.dean.jraw;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * A collection of utility methods
 */
public final class JrawUtils {
	private JrawUtils() {
		// no instances
	}

	/**
	 * Creates a new URL and prints the stack trace if a MalformedURLException is caught
	 *
	 * @param href The data of the URL
	 * @return A new URL
	 */
	public static URL newUrl(String href) {
		try {
			return new URL(href);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Creates a new URI and prints the stack trace if a URISyntaxException is caught
	 *
	 * @param location The location of the URI
	 * @return A new URI
	 */
	public static URI newUri(String location) {
		try {
			return new URI(location);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return null;
	}
}
