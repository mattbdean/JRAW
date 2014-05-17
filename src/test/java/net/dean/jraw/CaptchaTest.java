package net.dean.jraw;

import junit.framework.Assert;
import net.dean.jraw.models.Captcha;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class CaptchaTest {
	private RedditClient reddit;

	@BeforeTest
	public void setUp() {
		this.reddit = new RedditClient(TestUtils.getUserAgent(getClass()));
	}

	@Test
	public void testNeedsCaptchaWorking() {
		try {
			String[] credentials = TestUtils.getCredentials();
			reddit.login(credentials[0], credentials[1]);
			reddit.needsCaptcha();
		} catch (RedditException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testNonNullCaptchaComponents() {
		try {
			Captcha c = reddit.getNewCaptcha();
			Assert.assertNotNull(c.getId());
			Assert.assertNotNull(c.getImageStream());
		} catch (RedditException e) {
			Assert.fail(e.getMessage());
		}
	}
}
