package net.dean.jraw.test;

import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.LoggedInAccount;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * This class tests methods that deal with logging in and out, as well as testing the state.
 */
public class AuthenticationTest extends RedditTest {

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
