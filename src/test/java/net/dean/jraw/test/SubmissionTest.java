package net.dean.jraw.test;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.core.Comment;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Submission;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

public class SubmissionTest {
    private static final String ID = "92dd8";
    private RedditClient reddit;

    @BeforeClass
    public void setUp() {
        reddit = TestUtils.client(SubmissionTest.class);
    }

    @Test
    public void testCommentsNotNull() {
        try {
            Submission submission = reddit.getSubmission(ID);
            assertNotNull(submission);

            Listing<Comment> comments = submission.getComments();
            assertNotNull(comments, "Submission comments was null");
            // This is the most upvoted link in reddit history, there's bound to be more than one comment
            assertFalse(comments.isEmpty());

            Comment first = comments.get(0);
            ThingFieldTest.fieldValidityCheck(first);
        } catch (NetworkException e) {
            TestUtils.handle(e);
        }
    }

    @Test
    public void testRepliesNotNull() {
        try {
            Submission s = reddit.getSubmission(ID);

            Comment c = s.getComments().get(0);
            Assert.assertNotNull(c.getReplies().get(0).getBody());
        } catch (NetworkException e) {
            TestUtils.handle(e);
        }
    }

    @Test
    public void testRandom() {
        try {
            Submission s = reddit.getRandom();
            ThingFieldTest.fieldValidityCheck(s);

            s = reddit.getRandom("pics");
            ThingFieldTest.fieldValidityCheck(s);
        } catch (NetworkException e) {
            TestUtils.handle(e);
        }
    }
}
