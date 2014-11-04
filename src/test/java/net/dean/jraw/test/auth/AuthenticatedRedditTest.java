package net.dean.jraw.test.auth;

import net.dean.jraw.ApiException;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.managers.AccountManager;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.paginators.UserSubredditsPaginator;
import net.dean.jraw.test.RedditTest;
import net.dean.jraw.test.SetupRequiredException;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Provides a {@link LoggedInAccount} for unit tests and a way of getting the credentials found in
 * /src/test/resources/credentials.txt. Denotes that this set of tests require authentication.
 */
public abstract class AuthenticatedRedditTest extends RedditTest {
    private static String[] credentials;
    protected final AccountManager account;

    AuthenticatedRedditTest() {
        super();
        if (!reddit.isLoggedIn()) {
            login();
        }
        this.account = new AccountManager(reddit);
    }

    /**
     * Creates a new LoggedInAccount by logging in using the credentials provided by {@link #getCredentials()}
     */
    public void login() {
        try {
            String[] creds = getCredentials();
            reddit.login(creds[0], creds[1]);
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    /**
     * Gets the username and password for the testing user. If this is a Travis-CI build, then the environmental variable
     * called "USERNAME" will be used for the username, and "PASS" for the password. If else, a file called credentials.txt
     * (found in /src/test/resources) will be read for its first two lines. The first is the username, and the second
     * is the password.
     * @return A string array whose first element is a username and second is a password
     */
    protected final String[] getCredentials() {
        if (credentials != null) {
            return credentials;
        }
        try {
            // If running locally, use credentials file
            // If running with Travis-CI, use env variables
            if (System.getenv("TRAVIS") != null && Boolean.parseBoolean(System.getenv("TRAVIS"))) {
                return new String[] {System.getenv("USERNAME"), System.getenv("PASS")};
            } else {
                String[] details = new String[2];
                InputStream in = AuthenticatedRedditTest.class.getResourceAsStream("/credentials.txt");
                if (in == null) {
                    throw new SetupRequiredException("credentials.txt could not be found. See " +
                            "https://github.com/thatJavaNerd/JRAW#contributing for more information.");
                }
                Scanner s = new Scanner(in);
                details[0] = s.nextLine();
                details[1] = s.nextLine();
                s.close();
                credentials = details;
                return details;
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
        Listing<Subreddit> moderatorOf = new UserSubredditsPaginator(reddit, UserSubredditsPaginator.Where.MODERATOR).next();
        if (moderatorOf.size() == 0) {
            throw new IllegalStateException("Must be a moderator of at least one subreddit");
        }

        return moderatorOf.get(0);
    }
}
