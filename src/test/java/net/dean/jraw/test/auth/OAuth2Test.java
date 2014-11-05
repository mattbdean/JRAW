package net.dean.jraw.test.auth;

import net.dean.jraw.ApiException;
import net.dean.jraw.OAuth2RedditClient;
import net.dean.jraw.http.Credentials;
import net.dean.jraw.http.NetworkException;
import org.testng.annotations.Test;

public class OAuth2Test extends AuthenticatedRedditTest {
    private OAuth2RedditClient redditOAuth;

    public OAuth2Test() {
        this.redditOAuth = new OAuth2RedditClient(getUserAgent(getClass()));
    }

    @Test
    public void testLoginScript() {
        Credentials creds = getCredentials();
        try {
            validateModel(redditOAuth.login(Credentials.oauth2Script(
                    creds.getUsername(),
                    creds.getPassword(),
                    creds.getClientId(),
                    creds.getClientSecret())));
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }
}
