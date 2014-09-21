package net.dean.jraw.test;

import net.dean.jraw.ApiException;
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
            account.addDeveloper(CLIENT_ID, DEV_NAME);
        } catch (NetworkException | ApiException e) {
            TestUtils.handle(e);
        }
    }

    @Test(dependsOnMethods = "testAddDeveloper")
    public void testRemoveDeveloper() {
        try {
            account.removeDeveloper(CLIENT_ID, DEV_NAME);
        } catch (NetworkException | ApiException e) {
            TestUtils.handle(e);
        }
    }
}
