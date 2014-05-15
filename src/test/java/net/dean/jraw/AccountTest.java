package net.dean.jraw;

import net.dean.jraw.models.Account;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AccountTest {
	private static final String USER_AGENT = "JRAW Test Case for Accounts (v" + Constants.VERSION + ")";
	// Array length 2 where credentials[0] is the username and credentials[1] is the password
	private static String[] credentials;
	private RedditClient redditClient;

	@BeforeSuite
	public void getCredentials() {
		if (getClass().getResource("/credentials.txt") == null) {
			Assert.fail("Credentials file missing (/src/main/resources/credentials.txt)");
		}

		try {
			Path credPath = Paths.get(getClass().getResource("/credentials.txt").toURI());
			credentials = new String(Files.readAllBytes(credPath), "UTF-8").split("\n");
		} catch (IOException | URISyntaxException e) {
			Assert.fail(e.getMessage());
		}
	}

	@BeforeTest
	public void init() {
		redditClient = new RedditClient(USER_AGENT);
	}

	@Test
	public void login() {
		try {
			Account acc = redditClient.login(credentials[0], credentials[1]);
			Assert.assertNotNull(acc, "The account was null");
		} catch ( RedditException e) {
			Assert.fail(e.getMessage());
		}
	}
}
