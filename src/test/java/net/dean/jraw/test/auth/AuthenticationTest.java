package net.dean.jraw.test.auth;

import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.LoggedInAccount;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * This class tests methods that deal with logging in and out, as well as testing the state.
 */
public class AuthenticationTest extends AuthenticatedRedditTest {
    @Test
    @Override
    public LoggedInAccount login() {
        // Override this method to add @Test
        return super.login();
    }


    @Test
    public void testCurrentUsernameNotAvailable() {
        try {
            Assert.assertFalse(reddit.isUsernameAvailable(reddit.getAuthenticatedUser()), "Username was available");
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testMe() {
        try {
            validateModel(reddit.me());
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testIsLoggedIn() {
        Assert.assertTrue(reddit.isLoggedIn());
    }
}
