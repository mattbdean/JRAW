package net.dean.jraw.test;

import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.LoggedInAccount;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AppTest {
    private static LoggedInAccount account;
    private static String CLIENT_ID = "0fehncPayYTIIg";
    private static String DEV_NAME = "jraw_test2";

    @BeforeClass
    public static void setUp() {
        String[] creds = TestUtils.getCredentials();
        RedditClient reddit = TestUtils.client(AppTest.class);
        try {
            account = reddit.login(creds[0], creds[1]);
        } catch (NetworkException | ApiException e) {
            TestUtils.handle(e);
        }
    }

    @Test
    public void testAddDeveloper() {
        try {
            // Remove the developer to prevent /api/adddeveloper from returning a DEVELOPER_ALREADY_ADDED error.
            // /api/removedeveloper doesn't seem to return an error if the given name isn't in the list of current devs,
            // so this call will (probably) never fail.
            JrawUtils.logger().info("Removing developer if he/she is one so he/she can be added again");
            account.removeDeveloper(CLIENT_ID, DEV_NAME);
            // Actually test the method
            account.addDeveloper(CLIENT_ID, DEV_NAME);
        } catch (ApiException e) {
            if (!e.getCode().equals("DEVELOPER_ALREADY_ADDED")) {
                // https://github.com/thatJavaNerd/JRAW/issues/8
                TestUtils.handle(e);
            }
        } catch (NetworkException e) {
            TestUtils.handle(e);
        }
    }

    @Test
    public void testRemoveDeveloper() {
        // Add the developer if they're not already one
        try {
            account.addDeveloper(CLIENT_ID, DEV_NAME);
            JrawUtils.logger().info("Adding the developer so he/she can be removed");
        } catch (ApiException e) {
            if (!e.getCode().equals("DEVELOPER_ALREADY_ADDED")) {
                // Not ok
                TestUtils.handle(e);
            }
        } catch (NetworkException e) {
            TestUtils.handle(e);
        }

        try {
            account.removeDeveloper(CLIENT_ID, DEV_NAME);
        } catch (NetworkException | ApiException e) {
            TestUtils.handle(e);
        }
    }
}
