package net.dean.jraw.test;

import net.dean.jraw.RedditClient;
import net.dean.jraw.auth.NoSuchTokenException;
import net.dean.jraw.auth.RefreshTokenHandler;
import net.dean.jraw.auth.VolatileTokenStore;
import net.dean.jraw.http.UserAgent;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class RefreshTokenHandlerTest {

    @Test
    public void testSave() throws NoSuchTokenException {
        RefreshTokenHandler handler = new RefreshTokenHandler(new VolatileTokenStore(), new RedditClient(UserAgent.of("test")));
        assertFalse(handler.hasLastAuthenticated());

        String username = "testUsername";
        handler.writeToken(username, "token");
        assertTrue(handler.isStored(username));
        assertTrue(handler.hasLastAuthenticated());
        assertEquals(handler.getLastAuthenticatedUser(), username);

        String username2 = "testUsername2";
        handler.writeToken(username2, "token2");
        assertTrue(handler.isStored(username2));
        assertTrue(handler.hasLastAuthenticated());
        assertEquals(handler.getLastAuthenticatedUser(), username2);
    }
}
