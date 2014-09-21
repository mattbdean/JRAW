package net.dean.jraw.test;

import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.Captcha;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CaptchaTest {
    private static RedditClient reddit;

    @BeforeClass
    public static void setUp() {
        reddit = TestUtils.client(CaptchaTest.class);
        String[] credentials = TestUtils.getCredentials();
        try {
            reddit.login(credentials[0], credentials[1]);
        } catch (NetworkException | ApiException e) {
            TestUtils.handle(e);
        }
    }

    @Test
    public void testNeedsCaptcha() {
        try {
            reddit.needsCaptcha();
        } catch (NetworkException e) {
            TestUtils.handle(e);
        }
    }

    @Test
    public void testNonNullCaptchaComponents() {
        try {
            Captcha c = reddit.getNewCaptcha();
            Assert.assertNotNull(c.getId());
            Assert.assertNotNull(c.getImageStream());
        } catch (NetworkException e) {
            TestUtils.handle(e);
        }
    }
}
