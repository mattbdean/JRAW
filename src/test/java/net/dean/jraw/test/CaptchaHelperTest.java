package net.dean.jraw.test;

import net.dean.jraw.http.NetworkException;
import net.dean.jraw.managers.CaptchaHelper;
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
    public void testNeedsCaptcha() {
        try {
            // Make sure it doesn't error, could return true or false
            helper.isNecessary();
        } catch (NetworkException e) {
            handle(e);
        }
    }
}
