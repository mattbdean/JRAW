package net.dean.jraw.test;

import net.dean.jraw.*;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.SubmissionType;
import net.dean.jraw.models.VoteDirection;
import net.dean.jraw.models.core.Account;
import net.dean.jraw.models.core.Submission;
import net.dean.jraw.pagination.UserPaginatorSubmission;
import net.dean.jraw.pagination.Where;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.List;
import java.util.Optional;

public class AccountTest {
	// Array length 2 where credentials[0] is the username and credentials[1] is the password
	private static String[] credentials;
	private static RedditClient reddit;
	private static LoggedInAccount account;

	@BeforeClass
	public static void setUp() {
		if (AccountTest.class.getResource("/credentials.txt") == null) {
			Assert.fail("Credentials file missing (/src/main/resources/credentials.txt)");
		}

		credentials = TestUtils.getCredentials();

		reddit = TestUtils.client(AccountTest.class);
	}

	@Test
	public void login() {
		try {
			account = reddit.login(credentials[0], credentials[1]);
			Assert.assertNotNull(account, "The account was null");
			ThingFieldTest.fieldValidityCheck(account);
		} catch (NetworkException | ApiException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void getUserNotLoggedIn() {
		try {
			Account acc = reddit.getUser("thatJavaNerd");
			Assert.assertNotNull(acc, "The account was null");
		} catch (NetworkException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testUsernameNotAvailable() {
		try {
			Assert.assertFalse(reddit.isUsernameAvailable(credentials[0]), "Username was available");
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


	@Test
	public void testSendRepliesToInbox() throws ApiException {
		try {
			Submission s = reddit.getSubmission("28vvhm");
			LoggedInAccount me = reddit.login(TestUtils.getCredentials()[0], TestUtils.getCredentials()[1]);
			me.setSendRepliesToInbox(s, true);
		} catch (NetworkException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(dependsOnMethods = "login")
	public void testVote() {
		try {
			Submission submission = reddit.getSubmission("28d6vv");
			account.vote(submission, VoteDirection.NO_VOTE);
		} catch (NetworkException | ApiException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(dependsOnMethods = "login")
	public void testSaveSubmission() {
		try {
			Submission submission = reddit.getSubmission("28d6vv");
			account.save(submission);

			UserPaginatorSubmission paginator = new UserPaginatorSubmission.Builder(reddit)
					.username(account.getName())
					.where(Where.SAVED)
					.build();
			List<Submission> saved = paginator.next().getChildren();

			for (Submission s : saved) {
				if (s.getId().equals(submission.getId())) {
					return;
				}
			}

			Assert.fail("Did not find saved submission");

		} catch (NetworkException | ApiException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(dependsOnMethods = "testSaveSubmission")
	public void testUnsaveSubmission() {
		try {
			Submission submission = reddit.getSubmission("28d6vv");
			account.unsave(submission);

			UserPaginatorSubmission paginator = new UserPaginatorSubmission.Builder(reddit)
					.username(account.getName())
					.where(Where.SAVED)
					.build();
			List<Submission> saved = paginator.next().getChildren();

			// Search for the submission in the saved list
			saved.stream().filter(s -> s.getId().equals(submission.getId())).forEach(s -> Assert.fail("Found the submission after it was unsaved"));

		} catch (NetworkException | ApiException e) {
			Assert.fail(e.getMessage());
		}
	}
}
