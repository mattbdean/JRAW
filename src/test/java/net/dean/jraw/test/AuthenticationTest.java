package net.dean.jraw.test;

import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.LoggedInAccount;
import org.testng.Assert;
import org.testng.annotations.Test;

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
            Assert.assertFalse(reddit.isUsernameAvailable(account.getFullName()), "Username was available");
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
