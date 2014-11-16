package net.dean.jraw.test.auth;

import net.dean.jraw.AccountPreferencesEditor;
import net.dean.jraw.ApiException;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.AccountPreferences;
import net.dean.jraw.models.KarmaBreakdown;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.UserRecord;
import net.dean.jraw.paginators.Paginators;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class OAuth2Test extends AuthenticatedRedditTest {

    @Test
    public void testLoginScript() {
        try {
            redditOAuth2.revokeToken(getCredentials());
            validateModel(redditOAuth2.login(getCredentials()));
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRefreshToken() {
        try {
            // We're using script auth, which uses no refresh token
            redditOAuth2.refreshToken(getCredentials());
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testGetPreferences() {
        try {
            AccountPreferences prefs = redditOAuth2.getPreferences();
            validateModel(prefs);

            prefs = redditOAuth2.getPreferences("over_18", "research", "hide_from_robots");
            // Only these three should be not null
            assertNotNull(prefs.isOver18());
            assertNotNull(prefs.isResearchable());
            assertNotNull(prefs.isHiddenFromSearchEngines());

            // Anything else should be null
            assertNull(prefs.getLanguage());
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testUpdatePreferences() {
        try {
            AccountPreferences original = redditOAuth2.getPreferences();
            AccountPreferencesEditor prefs = new AccountPreferencesEditor(original);
            validateModel(redditOAuth2.updatePreferences(prefs));
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testGetTrophies() {
        try {
            validateModels(redditOAuth2.getTrophies());
            validateModels(redditOAuth2.getTrophies("spladug"));
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testKarmaBreakdown() {
        try {
            KarmaBreakdown breakdown = redditOAuth2.getKarmaBreakdown();
            validateModel(breakdown);
            validateModels(breakdown.getSummaries());
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testGetFriend() {
        try {
            validateModel(redditOAuth2.getFriend(getFriend().getFullName()));
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testAddFriend() {
        try {
            validateModel(redditOAuth2.updateFriend("thatJavaNerd"));
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testDeleteFriend() {
        try {
            redditOAuth2.deleteFriend(getFriend().getFullName());
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    private UserRecord getFriend() throws NetworkException {
        Listing<UserRecord> friends = Paginators.importantUsers(redditOAuth2, "friends").next();
        if (friends.size() == 0) {
            redditOAuth2.updateFriend("thatJavaNerd");
            return getFriend();
        }

        return friends.get(0);
    }
}
