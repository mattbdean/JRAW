package net.dean.jraw.test;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
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
			String[] submitText = reddit.getSubmitText("videos");
			Assert.assertTrue(submitText.length == 2);

			for (String str : submitText) {
				Assert.assertNotNull(str);
			}
		} catch (NetworkException e) {
			Assert.fail(e.getMessage());
		}
	}

}
