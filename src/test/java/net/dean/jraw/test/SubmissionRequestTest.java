package net.dean.jraw.test;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.CommentSort;
import net.dean.jraw.models.Submission;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Tests for SubmissionRequest and the responses it produces
 */
public class SubmissionRequestTest extends RedditTest {
    private static final String SUBMISSION_ID = "92dd8";
    private static final String FOCUS_COMMENT_ID = "c0b73aj";
    private RedditClient.SubmissionRequest request;
    private Submission s;

    @BeforeMethod
    public void setUp() {
        this.request = new RedditClient.SubmissionRequest(SUBMISSION_ID);
    }

    @Test
    public void testDepth() {
        // Change depth to only top-level comments
        request.depth(1);
        s = get();
        for (Comment c : s.getComments()) {
            if (c.getReplies() == null) {
                continue;
            }
            assertEquals(c.getReplies().size(), 0);
        }
    }

    @Test
    public void testFocus() {
        // Change the focused comment
        request.focus(FOCUS_COMMENT_ID);
        s = get();
        // The top level comment should be the focused one
        assertEquals(s.getComments().get(0).getId(), FOCUS_COMMENT_ID);
    }

    @Test
    public void testContext() {
        // Change the context while keeping the focus
        request.focus(FOCUS_COMMENT_ID);
        request.context(3);
        s = get();
        // The top level comment should be a parent of the focused one
        assertNotEquals(s.getComments().get(0).getId(), FOCUS_COMMENT_ID);
    }

    @Test
    public void testSort() {
        request.sort(CommentSort.TOP);
        s = get();
        int prevScore = s.getComments().get(0).getScore();
        for (int i = 1; i < 5; i++) {
            Comment c = s.getComments().get(i);
            assertTrue(prevScore >= c.getScore());
            prevScore = c.getScore();
        }
    }

    @Test
    public void testLimit() {
        request.limit(1);
        s = get();
        assertEquals(s.getComments().size(), 1);
    }

    private Submission get() {
        try {
            return reddit.getSubmission(request);
        } catch (NetworkException e) {
            handle(e);
            return null;
        }
    }
}
