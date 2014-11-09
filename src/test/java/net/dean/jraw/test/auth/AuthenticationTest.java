package net.dean.jraw.test.auth;

import net.dean.jraw.ApiException;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.LoggedInAccount;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * This class tests methods that deal with logging in and out, as well as testing the state.
 */
public class AuthenticationTest extends AuthenticatedRedditTest {

    @Test
    public void testLogout() {
        try {
            reddit.logout();
            assertFalse(reddit.isLoggedIn());
        } catch (NetworkException e) {
            handle(e);
        } finally {
            // Use try-catch-finally for this method only instead of the traditional setUp/tearDown testing methods
            // because this is the only test that logs out
            try {
                reddit.login(getCredentials());
            } catch (NetworkException | ApiException e) {
                handle(e);
            }
        }
    }

    @Test
    public void testCurrentUsernameNotAvailable() {
        try {
            assertFalse(reddit.isUsernameAvailable(reddit.getAuthenticatedUser()), "Username was available");
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
        try {
            LoggedInAccount acc = reddit.me();
            // /api/me.json returns '{}' when there is no logged in user
            boolean expected = acc.getDataNode() != null;

            assertEquals(reddit.isLoggedIn(), expected);
        } catch (NetworkException e) {
            handle(e);
        }
    }

}
