package net.dean.jraw.test.auth;

import net.dean.jraw.ApiException;
import net.dean.jraw.OAuth2RedditClient;
import net.dean.jraw.http.NetworkException;
import org.testng.annotations.Test;

public class OAuth2Test extends AuthenticatedRedditTest {
    private OAuth2RedditClient redditOAuth;

    public OAuth2Test() {
        this.redditOAuth = new OAuth2RedditClient(getUserAgent(getClass()));
        try {
            redditOAuth.login(getCredentials());
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    @Test
    public void testLoginScript() {
        try {
            redditOAuth.logout();
            validateModel(redditOAuth.login(getCredentials()));
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }
}
