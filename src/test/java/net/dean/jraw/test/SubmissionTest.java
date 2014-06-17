package net.dean.jraw.test;

import junit.framework.Assert;
import net.dean.jraw.NetworkException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.core.Comment;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Submission;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

public class SubmissionTest {
	private static final String ID = "92dd8";
	private RedditClient redditClient;

	@BeforeClass
	public void setUp() {
		redditClient = TestUtils.client(SubmissionTest.class);
	}

	@Test
	public void testCommentsNotNull() {
		try {
			Submission submission = redditClient.getSubmission(ID);
			assertNotNull(submission);

			Listing<Comment> comments = submission.getComments();
			assertNotNull(comments, "Submission comments was null");
			// This is the most upvoted link in reddit history, there's bound to be more than one comment
			assertFalse(comments.getChildren().isEmpty());

			Comment first = comments.getChildren().get(0);
			ThingFieldTest.fieldValidityCheck(first);
		} catch (NetworkException e) {
			Assert.fail(e.getMessage());
		}
	}
}
