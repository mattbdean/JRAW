package net.dean.jraw.test.auth;

import net.dean.jraw.ApiException;
import net.dean.jraw.OAuth2RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.AccountPreferences;
import net.dean.jraw.models.KarmaBreakdown;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

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
            redditOAuth.revokeToken(getCredentials());
            validateModel(redditOAuth.login(getCredentials()));
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    @Test
    public void testGetPreferences() {
        try {
            AccountPreferences prefs = redditOAuth.getPreferences();
            validateModel(prefs);

            prefs = redditOAuth.getPreferences("over_18", "research", "hide_from_robots");
            // Only these three should be not null
            assertNotNull(prefs.isOver18());
            assertNotNull(prefs.isResearchable());
            assertNotNull(prefs.isHiddenFromSearches());

            // Anything else should be null
            assertNull(prefs.getLanguage());
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testKarmaBreakdown() {
        try {
            KarmaBreakdown breakdown = redditOAuth.getKarmaBreakdown();
            validateModel(breakdown);
            validateModels(breakdown.getSummaries());
        } catch (NetworkException e) {
            handle(e);
        }
    }
}
