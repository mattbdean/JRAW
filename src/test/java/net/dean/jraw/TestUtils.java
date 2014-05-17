package net.dean.jraw;

import junit.framework.Assert;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class TestUtils {
	public static String[] getCredentials() {
		try {
			URL resource = TestUtils.class.getResource("/credentials.txt");
			Path credPath = Paths.get(resource.toURI());
			return new String(Files.readAllBytes(credPath), "UTF-8").split("\n");
		} catch (Exception e) {
			Assert.fail(e.getMessage());
			return null;
		}
	}

	public static String getUserAgent(Class<?> clazz) {
		return clazz.getSimpleName() + " for JRAW v" + Constants.VERSION;
	}
}
