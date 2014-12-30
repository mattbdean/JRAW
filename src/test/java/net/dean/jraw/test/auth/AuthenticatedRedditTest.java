package net.dean.jraw.test.auth;

import net.dean.jraw.ApiException;
import net.dean.jraw.RedditOAuth2Client;
import net.dean.jraw.http.AuthenticationMethod;
import net.dean.jraw.http.Credentials;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.managers.AccountManager;
import net.dean.jraw.managers.ModerationManager;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.paginators.Paginators;
import net.dean.jraw.test.RedditTest;
import net.dean.jraw.test.SetupRequiredException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.InputStream;

/**
 * Provides a {@link LoggedInAccount} for unit tests and a way of getting the credentials found in
 * /src/test/resources/credentials.json. Denotes that this set of tests require authentication.
 */
public abstract class AuthenticatedRedditTest extends RedditTest {
    private static Credentials credentials;
    private static ObjectMapper objectMapper = new ObjectMapper();
    protected final AccountManager account;
    protected final ModerationManager moderation;
    protected static final RedditOAuth2Client redditOAuth2 = new RedditOAuth2Client("");

    AuthenticatedRedditTest() {
        super();
        redditOAuth2.setUserAgent(getUserAgent(getClass()));
        Credentials creds = getCredentials();
        try {
            if (!reddit.isLoggedIn()) {
                reddit.login(Credentials.standard(creds.getUsername(), creds.getPassword()));
            }
            if (!redditOAuth2.isLoggedIn()) {
                redditOAuth2.login(credentials);
            }
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
        this.account = new AccountManager(reddit);
        this.moderation = new ModerationManager(reddit);
    }

    /**
     * Gets a Credentials object that represents the properties found in credentials.json
     * (located at /src/test/resources). The Credential's AuthenticationMethod will always be
     * {@link AuthenticationMethod#SCRIPT}
     *
     * @return A Credentials object parsed form credentials.json
     */
    protected final Credentials getCredentials() {
        if (credentials != null) {
            return credentials;
        }

        try {
            // If running locally, use credentials file
            // If running with Travis-CI, use env variables
            if (System.getenv("TRAVIS") != null && Boolean.parseBoolean(System.getenv("TRAVIS"))) {
                credentials = Credentials.script(System.getenv("USERNAME"),
                        System.getenv("PASS"),
                        System.getenv("CLIENT_ID"),
                        System.getenv("CLIENT_SECRET"));
                return credentials;
            } else {
                InputStream in = AuthenticatedRedditTest.class.getResourceAsStream("/credentials.json");
                if (in == null) {
                    throw new SetupRequiredException("credentials.json could not be found. See " +
                            "https://github.com/thatJavaNerd/JRAW#contributing for more information.");
                }

                JsonNode data = objectMapper.readTree(in);
                credentials = Credentials.script(data.get("username").asText(),
                        data.get("password").asText(),
                        data.get("client_id").asText(),
                        data.get("client_secret").asText());
                return credentials;
            }
        } catch (Exception e) {
            handle(e);
            return null;
        }
    }

    /**
     * Gets a subreddit that the testing user moderates
     * @return A subreddit
     */
    protected final Subreddit getModeratedSubreddit() {
        Listing<Subreddit> moderatorOf = Paginators.mySubreddits(reddit, "moderator").next();
        if (moderatorOf.size() == 0) {
            throw new IllegalStateException("Must be a moderator of at least one subreddit");
        }

        return moderatorOf.get(0);
    }
}
