package net.dean.jraw.test.auth;

import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.Contribution;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.VoteDirection;
import net.dean.jraw.models.core.Comment;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Submission;
import net.dean.jraw.pagination.UserContributionPaginator;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.List;

import static net.dean.jraw.pagination.UserContributionPaginator.Where;
import static org.testng.Assert.*;

/**
 * This class tests methods that require authentication, such as voting, saving, hiding, and posting.
 */
public class AuthenticatedUserTest extends AuthenticatedRedditTest {
    private static final String SUBMISSION_ID = "262la4";
    private static final String COMMENT_ID = "cieys70";
    private static String CLIENT_ID = "0fehncPayYTIIg";
    private static String DEV_NAME = "jraw_test2";
    private String newSubmssionId;
    private String newCommentId;

    @Test
    public void testPostLink() {
        try {
            int number = randomInt();

            URL url = JrawUtils.newUrl("https://www.google.com/?q=" + number);

            Submission submission = account.submitContent(
                    new LoggedInAccount.SubmissionBuilder(url, "jraw_testing2", "Link post test (random=" + number + ")"));

            assertTrue(!submission.isSelfPost());
            assertTrue(submission.getUrl().equals(url));
            validateModel(submission);
        } catch (NetworkException e) {
            handle(e);
        } catch (ApiException e) {
            handlePostingQuota(e);
        }
    }

    @Test
    public void testPostSelfPost() {
        try {
            int number = randomInt();
            String content = reddit.getUserAgent();

            Submission submission = account.submitContent(
                    new LoggedInAccount.SubmissionBuilder(content, "jraw_testing2", "Self post test (random=" + number + ")"));

            assertTrue(submission.isSelfPost());
            assertTrue(submission.getSelftext().md().equals(content));
            validateModel(submission);
            this.newSubmssionId = submission.getFullName();
        } catch (NetworkException e) {
            handle(e);
        } catch (ApiException e) {
            handlePostingQuota(e);
        }
    }

    @Test
    public void testReplySubmission() {
        try {
            String replyText = "" + randomInt();
            Submission submission = reddit.getSubmission(SUBMISSION_ID);

            // Reply to a submission
            this.newCommentId = account.reply(submission, replyText);
            assertTrue(JrawUtils.isFullName(newCommentId));
        } catch (ApiException e) {
            handlePostingQuota(e);
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testReplyComment() {
        try {
            Listing<Comment> comments = reddit.getSubmission(SUBMISSION_ID).getComments();
            Comment replyTo = null;
            for (Comment c : comments) {
                if (c.getId().equals(COMMENT_ID)) {
                    replyTo = c;
                    break;
                }
            }

            assertNotNull(replyTo);
            assertNotNull(account.reply(replyTo, ""+randomInt()));
        } catch (NetworkException e) {
            handle(e);
        } catch (ApiException e) {
            handlePostingQuota(e);
        }
    }

    @Test(dependsOnMethods = "testReplySubmission")
    public void testDeleteComment() {
        try {
            account.delete(newCommentId);

            for (Comment c : reddit.getSubmission(SUBMISSION_ID).getComments()) {
                if (c.getId().equals(COMMENT_ID)) {
                    fail("Found the (supposedly) deleted comment");
                }
            }
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    @Test(dependsOnMethods = "testPostSelfPost")
    public void testDeletePost() {
        try {
            account.delete(newSubmssionId);
        } catch (NetworkException | ApiException e) {
            handle(e);
        }

        try {
            reddit.getSubmission(newSubmssionId);
        } catch (NetworkException e) {
            if (e.getCode() != 404) {
                fail("Did not get a 404 when querying the deleted submission", e);
            }
        }
    }


    @Test
    public void testSendRepliesToInbox() throws ApiException {
        try {
            Submission s = (Submission) getPaginator(Where.SUBMITTED).next().get(0);
            account.setSendRepliesToInbox(s, true);
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testVote() {
        try {
            String submissionId = "28d6vv";
            Submission submission = reddit.getSubmission(submissionId);

            // Figure out a new vote direction: up if there is no vote, no vote if upvoted
            VoteDirection newVoteDirection = submission.getVote() == VoteDirection.NO_VOTE ? VoteDirection.UPVOTE : VoteDirection.NO_VOTE;
            account.vote(submission, newVoteDirection);

            submission = reddit.getSubmission(submissionId);
            // Make sure the vote took effect
            assertEquals(submission.getVote(), newVoteDirection);
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    @Test
    public void testSaveSubmission() {
        try {
            Submission submission = reddit.getSubmission("28d6vv");
            account.save(submission, true);

            UserContributionPaginator paginator = getPaginator(Where.SAVED);
            List<Contribution> saved = paginator.next();

            for (Contribution c : saved) {
                Submission s = (Submission) c;
                if (s.getId().equals(submission.getId())) {
                    return;
                }
            }

            fail("Did not find saved submission");

        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    @Test(dependsOnMethods = "testSaveSubmission")
    public void testUnsaveSubmission() {
        try {
            Submission submission = reddit.getSubmission("28d6vv");
            account.save(submission, false);

            UserContributionPaginator paginator = getPaginator(Where.SAVED);
            List<Contribution> saved = paginator.next();

            // Fail if we find the submission in the list
            saved.stream().filter(s -> s.getId().equals(submission.getId())).forEach(s ->
                    fail("Found the submission after it was unsaved"));

        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    @Test
    public void testHideSubmission() {
        try {
            Submission submission = reddit.getSubmission("28d6vv");
            account.hide(submission, true);

            UserContributionPaginator paginator = getPaginator(Where.HIDDEN);
            List<Contribution> hidden = paginator.next();

            for (Contribution c : hidden) {
                Submission s = (Submission) c;
                if (s.getId().equals(submission.getId())) {
                    return;
                }
            }

            fail("Did not find the submission in the hidden posts");
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    @Test(dependsOnMethods = "testHideSubmission")
    public void testUnhideSubmission() {
        try {
            Submission submission = reddit.getSubmission("28d6vv");
            account.hide(submission, false);

            UserContributionPaginator paginator = getPaginator(Where.HIDDEN);
            List<Contribution> hidden = paginator.next();

            for (Contribution c : hidden) {
                Submission s = (Submission) c;
                if (s.getId().equals(submission.getId())) {
                    fail("Found unhidden submission in hidden posts");
                }
            }
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    @Test
    public void testAddDeveloper() {
        try {
            // Remove the developer to prevent /api/adddeveloper from returning a DEVELOPER_ALREADY_ADDED error.
            // /api/removedeveloper doesn't seem to return an error if the given name isn't in the list of current devs,
            // so this call will (probably) never fail.
            JrawUtils.logger().info("Removing developer if he/she is one so he/she can be added again");
            account.removeDeveloper(CLIENT_ID, DEV_NAME);
            // Actually test the method
            account.addDeveloper(CLIENT_ID, DEV_NAME);
        } catch (ApiException e) {
            if (!e.getCode().equals("DEVELOPER_ALREADY_ADDED")) {
                // https://github.com/thatJavaNerd/JRAW/issues/8
                handle(e);
            }
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testRemoveDeveloper() {
        // Add the developer if they're not already one
        try {
            account.addDeveloper(CLIENT_ID, DEV_NAME);
            JrawUtils.logger().info("Adding the developer so he/she can be removed");
        } catch (ApiException e) {
            if (!e.getCode().equals("DEVELOPER_ALREADY_ADDED")) {
                // Not ok
                handle(e);
            }
        } catch (NetworkException e) {
            handle(e);
        }

        try {
            account.removeDeveloper(CLIENT_ID, DEV_NAME);
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    @Test
    public void testSetNsfw() {
        try {
            Submission s = (Submission) getPaginator(Where.SUBMITTED).next().get(0);
            boolean newVal = !s.isNSFW();

            account.setNsfw(s, newVal);

            // Reload the submission's data
            s = reddit.getSubmission(s.getId());
            assertTrue(s.isNSFW() == newVal);
        } catch (NetworkException | ApiException e) {
            handle(e);
        }

    }

    private UserContributionPaginator getPaginator(Where where) {
        return new UserContributionPaginator(reddit, where, account.getFullName());
    }
}
