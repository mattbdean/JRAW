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
import net.dean.jraw.models.Contribution;
import net.dean.jraw.pagination.UserContributionPaginator;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.List;

import static net.dean.jraw.pagination.UserContributionPaginator.Where;

public class AccountTest {
    // Array length 2 where credentials[0] is the username and credentials[1] is the password
    private static String[] credentials;
    private static RedditClient reddit;
    private static LoggedInAccount account;
    private String commentId;

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
            TestUtils.handleApiException(e);
        }
    }

    @Test(dependsOnMethods = "login")
    public void testReply() {
        try {
            String submissionId = "262la4";

            String replyText = "" + TestUtils.randomInt();
            Submission submission = reddit.getSubmission(submissionId);

            // Reply to a submission
            this.commentId = account.reply(submission, replyText);
            Assert.assertTrue(JrawUtils.isFullName(commentId));
        } catch (ApiException e) {
            TestUtils.handleApiException(e);
        } catch (NetworkException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test(dependsOnMethods = "testReply")
    public void testDeleteComment() {
        try {
            account.delete(commentId);
        } catch (NetworkException | ApiException e) {
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void testSendRepliesToInbox() throws ApiException {
        try {
            Submission s = (Submission) getPaginator(Where.SUBMITTED).next().getChildren().get(0);
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
            account.save(submission, true);

            UserContributionPaginator paginator = getPaginator(Where.SAVED);
            List<Contribution> saved = paginator.next().getChildren();

            for (Contribution c : saved) {
                Submission s = (Submission) c;
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
            account.save(submission, false);

            UserContributionPaginator paginator = getPaginator(Where.SAVED);
            List<Contribution> saved = paginator.next().getChildren();

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
            account.hide(submission, true);

            UserContributionPaginator paginator = getPaginator(Where.HIDDEN);
            List<Contribution> hidden = paginator.next().getChildren();

            for (Contribution c : hidden) {
                Submission s = (Submission) c;
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
            account.hide(submission, false);

            UserContributionPaginator paginator = getPaginator(Where.HIDDEN);
            List<Contribution> hidden = paginator.next().getChildren();

            for (Contribution c : hidden) {
                Submission s = (Submission) c;
                if (s.getId().equals(submission.getId())) {
                    Assert.fail("Found unhidden submission in hidden posts");
                }
            }
        } catch (NetworkException | ApiException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testMyMultis() {
        try {
            List<MultiReddit> multis = account.getMyMultiReddits();

            multis.forEach(ThingFieldTest::fieldValidityCheck);
        } catch (NetworkException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testMultis() {
        try {
            MultiReddit multi = reddit.getPublicMulti(account.getFullName(), "test_multi");
            ThingFieldTest.fieldValidityCheck(multi);

            TestUtils.testRenderString(reddit.getPublicMultiDescription(account.getFullName(), "test_multi"));
        } catch (NetworkException | ApiException e) {
            Assert.fail(e.getMessage());
        }
    }

    private UserContributionPaginator getPaginator(Where where) {
        return new UserContributionPaginator(reddit, where, account.getFullName());
    }
}
