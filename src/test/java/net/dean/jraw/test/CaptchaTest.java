package net.dean.jraw.test;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RedditResponse;
import net.dean.jraw.models.Captcha;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * This class tests anything and everything related to captchas.
 */
public class CaptchaTest extends RedditTest {

    @Test
    public void testNeedsCaptcha() {
        try {
            // Make sure it doesn't error, could return true or false
            reddit.needsCaptcha();
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testCaptchaComponents() {
        try {
            Captcha c = reddit.getNewCaptcha();
            Assert.assertNotNull(c.getId());
            Assert.assertNotNull(c.getImageUrl());

            // Test out the image URL
            Request imageRequest = reddit.request()
                    .url(c.getImageUrl())
                    .get()
                    .build();
            RedditResponse response = reddit.execute(imageRequest);

            MediaType actual = response.getType();
            MediaType expected = MediaType.parse("image/png");
            Assert.assertTrue(JrawUtils.typeComparison(actual, expected));
        } catch (NetworkException e) {
            handle(e);
        }
    }
}
