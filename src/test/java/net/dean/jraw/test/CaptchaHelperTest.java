package net.dean.jraw.test;

import com.google.common.net.MediaType;
import net.dean.jraw.ApiException;
import net.dean.jraw.http.HttpRequest;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.managers.CaptchaHelper;
import net.dean.jraw.models.Captcha;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * This class tests anything and everything related to captchas.
 */
public class CaptchaHelperTest extends RedditTest {
    private CaptchaHelper helper;

    @BeforeMethod
    public void setUp() {
        this.helper = new CaptchaHelper(reddit);
    }

    @Test
    public void testById() {
        try {
            CaptchaHelper helper = new CaptchaHelper(reddit);
            Captcha c = helper.getNew();
            helper.get(c.getId());
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    @Test
    public void testCaptchaComponents() {
        try {
            Captcha c = helper.getNew();
            Assert.assertNotNull(c.getId());
            Assert.assertNotNull(c.getImageUrl());

            // Test out the image URL
            HttpRequest imageRequest = HttpRequest.Builder.from("GET", c.getImageUrl())
                    .expected(MediaType.parse("image/png"))
                    .build();
            reddit.execute(imageRequest);
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    @Test
    public void testNeedsCaptcha() {
        try {
            // Make sure it doesn't error, could return true or false
            helper.isNecessary();
        } catch (NetworkException e) {
            handle(e);
        }
    }
}
