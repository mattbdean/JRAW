package net.dean.jraw.test;

import net.dean.jraw.RedditClient;
import net.dean.jraw.auth.*;
import net.dean.jraw.http.UserAgent;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class AuthenticationManagerTest {
    private TokenStore tokenStore;

    @BeforeMethod
    public void setUp() {
        tokenStore = new VolatileTokenStore();
        RedditClient reddit = new RedditClient(UserAgent.of("test"));
        AuthenticationManager.get().init(reddit, new RefreshTokenHandler(tokenStore, reddit));
    }

    @Test
    public void testCheckAuthState() {
        assertEquals(AuthenticationManager.get().checkAuthState(), AuthenticationState.NONE);

        // Emulate a user being authenticated
        tokenStore.writeToken("refresh_token_username1", "token1");
        tokenStore.writeToken("last_user", "username1");

        // Not authenticated but has a refresh token
        assertEquals(AuthenticationManager.get().checkAuthState(), AuthenticationState.NEED_REFRESH);
    }
}
