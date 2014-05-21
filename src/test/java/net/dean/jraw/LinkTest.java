package net.dean.jraw;

import junit.framework.Assert;
import net.dean.jraw.models.core.Comment;
import net.dean.jraw.models.core.Link;
import net.dean.jraw.models.core.Listing;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

public class LinkTest {

	private RedditClient redditClient;

	@BeforeTest
	public void setUp() {
		redditClient = new RedditClient(TestUtils.getUserAgent(getClass()));
	}

	@Test
	public void testCommentsNotNull() {
		try {
			Link link = redditClient.getLink("92dd8");
			assertNotNull(link);

			Listing<Comment> comments = link.getComments();
			assertNotNull(comments, "Link comments was null");
			// This is the most upvoted link in reddit history, there's bound to be more than one comment
			assertFalse(comments.getChildren().isEmpty());

			Comment first = comments.getChildren().get(0);
			ThingFieldTest.fieldValidityCheck(first);
		} catch (NetworkException e) {
			Assert.fail(e.getMessage());
		}
	}
}
