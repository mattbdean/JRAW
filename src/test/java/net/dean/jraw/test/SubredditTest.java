package net.dean.jraw.test;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.RenderStringPair;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SubredditTest {

	private RedditClient reddit;

	@BeforeClass
	public void setUp() {
		reddit = TestUtils.client(SubredditTest.class);
	}

	@Test
	public void testSubmitText() {
		try {
			RenderStringPair submitText = reddit.getSubmitText("videos");
			TestUtils.testRenderString(submitText);
		} catch (NetworkException e) {

			Assert.fail(e.getMessage());
		}
	}

}
