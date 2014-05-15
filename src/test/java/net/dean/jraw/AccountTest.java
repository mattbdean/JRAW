package net.dean.jraw;

import net.dean.jraw.models.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RunWith(JUnit4.class)
public class AccountTest {
	private static final String USER_AGENT = "JRAW Test Case for Accounts (v" + Constants.VERSION + ")";
	private RedditClient redditClient;

	@Before
	public void init() {
		redditClient = new RedditClient(USER_AGENT);
	}

	@Test
	public void login() {
		if (getClass().getResource("/credentials.txt") == null) {
			Assert.fail("Credentials file missing (/src/main/resources/credentials.txt)");
		}
		try {
			Path credPath = Paths.get(getClass().getResource("/credentials.txt").toURI());
			String[] creds = new String(Files.readAllBytes(credPath), "UTF-8").split("\n");

			Account acc = redditClient.login(creds[0], creds[1]);
			Assert.assertNotNull("The account was null", acc);
		} catch (IOException | URISyntaxException | RedditException e) {
			Assert.fail(e.getMessage());
		}

	}
}
