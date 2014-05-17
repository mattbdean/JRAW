package net.dean.jraw;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.MissingResourceException;

public final class TestUtils {
	public static String[] getCredentials() throws MissingResourceException, URISyntaxException, IOException {
		URL resource = TestUtils.class.getResource("/credentials.txt");
		Path credPath = Paths.get(resource.toURI());
		return new String(Files.readAllBytes(credPath), "UTF-8").split("\n");
	}

	public static String getUserAgent(Class<?> clazz) {
		return clazz.getSimpleName() + " for JRAW v" + Constants.VERSION;
	}
}
