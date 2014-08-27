package net.dean.jraw.test;

import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.LoggedInAccount;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AppTest {
    private static LoggedInAccount account;
    private static String[] credentials = TestUtils.getCredentials();

    @BeforeClass
    public static void setUp() {
        RedditClient reddit = TestUtils.client(AppTest.class);
        try {
            account = reddit.login(credentials[0], credentials[1]);
        } catch (NetworkException | ApiException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testAddDeveloper() {
        try {
            account.addDeveloper("0fehncPayYTIIg", "jraw_test2");
        } catch (NetworkException | ApiException e) {
            e.printStackTrace();
        }
    }

    @Test(dependsOnMethods = "testAddDeveloper")
    public void testRemoveDeveloper() {
        try {
            account.removeDeveloper("0fehncPayYTIIg", "jraw_test2");
        } catch (NetworkException | ApiException e) {
            e.printStackTrace();
        }
    }
}
