package net.dean.jraw.test;

import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.MultiReddit;
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

public class AccountTest {
	// Array length 2 where credentials[0] is the username and credentials[1] is the password
	private static String[] credentials;
	private static RedditClient reddit;
	private static LoggedInAccount account;

	@BeforeClass
	public static void setUp() {
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

			URL url = JrawUtils.newUrl("https://www.google.com/?q=" + number);

			Submission submission = account.submitContent(
					new LoggedInAccount.SubmissionBuilder(url, "jraw_testing2", "Link post test (random=" + number + ")"));

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
			account.setSaved(submission, true);

			UserPaginatorSubmission paginator = getPaginator(Where.SAVED);
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
			account.setSaved(submission, false);

			UserPaginatorSubmission paginator = getPaginator(Where.SAVED);
			List<Submission> saved = paginator.next().getChildren();

			// Search for the submission in the saved list
			saved.stream().filter(s -> s.getId().equals(submission.getId())).forEach(s -> Assert.fail("Found the submission after it was unsaved"));

		} catch (NetworkException | ApiException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testHideSubmission() {
		try {
			Submission submission = reddit.getSubmission("28d6vv");
			account.setHidden(submission, true);

			UserPaginatorSubmission paginator = getPaginator(Where.HIDDEN);
			List<Submission> hidden = paginator.next().getChildren();

			for (Submission s : hidden) {
				if (s.getId().equals(submission.getId())) {
					return;
				}
			}

			Assert.fail("Did not find the submission in the hidden posts");
		} catch (NetworkException | ApiException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(dependsOnMethods = "testHideSubmission")
	public void testUnhideSubmission() {
		try {
			Submission submission = reddit.getSubmission("28d6vv");
			account.setHidden(submission, false);

			UserPaginatorSubmission paginator = getPaginator(Where.HIDDEN);
			List<Submission> hidden = paginator.next().getChildren();

			hidden.stream().filter(s -> s.getId().equals(submission.getId())).forEach(s -> Assert.fail("Found "));
		} catch (NetworkException | ApiException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testMyMultis() {
		try {
			List<MultiReddit> multis = account.getMyMultis();

			multis.forEach(ThingFieldTest::fieldValidityCheck);
		} catch (NetworkException e) {
			Assert.fail(e.getMessage());
		}
	}

	private UserPaginatorSubmission getPaginator(Where where) {
		return new UserPaginatorSubmission.Builder(reddit)
				.username(account.getName())
				.where(where)
				.build();
	}
}
