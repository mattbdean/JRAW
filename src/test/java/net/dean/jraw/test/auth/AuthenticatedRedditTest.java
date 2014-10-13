package net.dean.jraw.test.auth;

import net.dean.jraw.ApiException;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.managers.AccountManager;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.test.RedditTest;

import java.io.FileNotFoundException;
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
     * @return A LoggedInAccount to be used throughout the test class
     */
    public LoggedInAccount login() {
        try {
            String[] creds = getCredentials();
            return reddit.login(creds[0], creds[1]);
        } catch (NetworkException | ApiException e) {
            handle(e);
            return null;
        }
    }

    /**
     * Gets the username and password for the testing user. If this is a Travis-CI build, then the environmental variable
     * called "USERNAME" will be used for the username, and "PASS" for the password. If else, a file called credentials.txt
     * (found in /src/test/resources) will be read for its first two lines. The first is the username, and the second
     * is the password.
     * @return A string array whose first element is a username and second is a password
     */
    public String[] getCredentials() {
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
                    throw new FileNotFoundException("credentials.txt could not be found. See " +
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
}
