package net.dean.jraw;

import net.dean.jraw.models.SubmissionType;
import net.dean.jraw.models.core.Account;
import net.dean.jraw.models.core.Link;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.Optional;

public class AccountTest {
	// Array length 2 where credentials[0] is the username and credentials[1] is the password
	private static String[] credentials;
	private RedditClient redditClient;

	@BeforeSuite
	public void getCredentials() {
		if (getClass().getResource("/credentials.txt") == null) {
			Assert.fail("Credentials file missing (/src/main/resources/credentials.txt)");
		}

		credentials = TestUtils.getCredentials();
	}

	@BeforeClass
	public void setUp() {
		redditClient = new RedditClient(TestUtils.getUserAgent(getClass()));
	}

	@Test
	public void login() {
		try {
			Account acc = redditClient.login(credentials[0], credentials[1]);
			Assert.assertNotNull(acc, "The account was null");
		} catch (NetworkException | ApiException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(dependsOnMethods = "login")
	public void getUserNotLoggedIn() {
		try {
			Account acc = redditClient.getUser("thatJavaNerd");
			Assert.assertNotNull(acc, "The account was null");
		} catch (NetworkException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(dependsOnMethods = "login")
	public void testPostLink() {
		try {
			int number = TestUtils.randomInt();

			Optional<URL> url = Optional.of(JrawUtils.newUrl("https://www.google.com/?q=" + number));
			Optional<String> text = Optional.empty();

			Link link = redditClient.submitLink(SubmissionType.LINK, url, text, "jraw_testing2", "Link post test (random:"+number+")",
					false, false, false);
			Assert.assertNotNull(link);
			ThingFieldTest.fieldValidityCheck(link);
		} catch (NetworkException e) {

			Assert.fail(e.getMessage());
		} catch (ApiException e) {
			TestUtils.ignoreRatelimitQuotaFilled(e);
		}
	}
}
