package net.dean.jraw;

import net.dean.jraw.models.core.Account;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Random;

public class AccountTest {
	// Array length 2 where credentials[0] is the username and credentials[1] is the password
	private static String[] credentials;
	private RedditClient redditClient;
	private Random random;

	@BeforeSuite
	public void getCredentials() {
		if (getClass().getResource("/credentials.txt") == null) {
			Assert.fail("Credentials file missing (/src/main/resources/credentials.txt)");
		}

		credentials = TestUtils.getCredentials();
		random = new Random();
	}

	@BeforeTest
	public void setUp() {
		redditClient = new RedditClient(TestUtils.getUserAgent(getClass()));
	}

	@Test
	public void login() {
		try {
			Account acc = redditClient.login(credentials[0], credentials[1]);
			Assert.assertNotNull(acc, "The account was null");
		} catch (RedditException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void getUserNotLoggedIn() {
		try {
			Account acc = redditClient.getUser("thatJavaNerd");
			Assert.assertNotNull(acc, "The account was null");
		} catch (RedditException e) {
			Assert.fail(e.getMessage());
		}
	}
}
