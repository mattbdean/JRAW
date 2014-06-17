package net.dean.jraw.test;

import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.NetworkException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.SubmissionType;
import net.dean.jraw.models.core.Account;
import net.dean.jraw.models.core.Submission;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.Optional;

public class AccountTest {
	// Array length 2 where credentials[0] is the username and credentials[1] is the password
	private static String[] credentials;
	private static RedditClient redditClient;
	private static LoggedInAccount account;

	@BeforeClass
	public static void setUp() {
		if (AccountTest.class.getResource("/credentials.txt") == null) {
			Assert.fail("Credentials file missing (/src/main/resources/credentials.txt)");
		}

		credentials = TestUtils.getCredentials();

		redditClient = TestUtils.client(AccountTest.class);
	}

	@Test
	public void login() {
		try {
			account = redditClient.login(credentials[0], credentials[1]);
			Assert.assertNotNull(account, "The account was null");
			ThingFieldTest.fieldValidityCheck(account);
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

			Submission submission = account.submitContent(SubmissionType.LINK, url, text, "jraw_testing2", "Link post test (random=" + number + ")",
					false, false, false);
			Assert.assertNotNull(submission);
			ThingFieldTest.fieldValidityCheck(submission);
		} catch (NetworkException e) {
			Assert.fail(e.getMessage());
		} catch (ApiException e) {
			TestUtils.ignoreRatelimitQuotaFilled(e);
		}
	}
}
