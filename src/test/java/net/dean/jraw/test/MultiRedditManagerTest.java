package net.dean.jraw.test;

import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.MultiRedditManager;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.MultiReddit;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class MultiRedditManagerTest {

    private static LoggedInAccount account;
    private static MultiRedditManager manager;
    private static final String MULTI_NAME = "jraw_testing";
    private static final List<String> MULTI_INITIAL_SUBS = Arrays.asList("funny", "pics");

    @BeforeClass
    public static void setUp() throws NetworkException, ApiException {
        String[] credentials = TestUtils.getCredentials();
        RedditClient reddit = TestUtils.client(AccountTest.class);
        account = reddit.login(credentials[0], credentials[1]);
        manager = new MultiRedditManager(account);
    }

    @Test
    public void testMyMultis() {
        try {
            List<MultiReddit> multis = manager.mine();
            multis.forEach(ThingFieldTest::fieldValidityCheck);
        } catch (NetworkException | ApiException e) {
            TestUtils.handle(e);
        }
    }

    @Test
    public void testCreate() {
        try {
            // Delete if exists
            if (multiExists(MULTI_NAME)) {
                JrawUtils.logger().info("Deleting existing multi");
                manager.delete(MULTI_NAME);
            }

            manager.create(MULTI_NAME, MULTI_INITIAL_SUBS, true);

            Assert.assertTrue(multiExists(MULTI_NAME));
        } catch (NetworkException | ApiException e) {
            TestUtils.handle(e);
        }
    }

    @Test
    public void testDelete() {
        try {
            // Create if does not exist
            if (!multiExists(MULTI_NAME)) {
                JrawUtils.logger().info("Creating the multi so it can be created");
                manager.create(MULTI_NAME, MULTI_INITIAL_SUBS, true);
            }

            // Actually test the method
            manager.delete(MULTI_NAME);

            Assert.assertFalse(multiExists(MULTI_NAME));
        } catch (ApiException | NetworkException e) {
            TestUtils.handle(e);
        }
    }

    @Test
    public void testMultis() {
        try {
            MultiReddit multi = manager.get("test_multi");
            ThingFieldTest.fieldValidityCheck(multi);

            TestUtils.testRenderString(manager.getDescription(account.getFullName(), "test_multi"));
        } catch (NetworkException | ApiException e) {
            TestUtils.handle(e);
        }
    }

    private boolean multiExists(String name) throws NetworkException, ApiException {
        for (MultiReddit mine : manager.mine()) {
            if (mine.getFullName().equals(name)) {
                return true;
            }
        }

        return false;
    }
}
