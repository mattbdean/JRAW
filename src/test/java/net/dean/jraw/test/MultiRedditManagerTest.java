package net.dean.jraw.test;

import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.MultiRedditManager;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.MultiReddit;
import net.dean.jraw.models.RenderStringPair;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class MultiRedditManagerTest {

    private static LoggedInAccount account;
    private static MultiRedditManager manager;
    private static final String MULTI_NAME = "fdsafdsafds";
    private static String readOnlyMulti;
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
        } catch (ApiException e) {
            if (!e.getCode().equals("MULTI_EXISTS")) {
                // https://github.com/thatJavaNerd/JRAW/issues/7
                TestUtils.handle(e);
            }
        } catch (NetworkException e) {
            TestUtils.handle(e);
        }
    }

    @Test
    public void testDelete() {
        try {
            // Create if does not exist
            if (!multiExists(MULTI_NAME)) {
                JrawUtils.logger().info("Creating the multi so it can be deleted");
                manager.create(MULTI_NAME, MULTI_INITIAL_SUBS, true);
            }
        } catch (ApiException e) {
            if (!e.getCode().equals("MULTI_EXISTS")) {
                TestUtils.handle(e);
            }
        } catch (NetworkException e) {
            TestUtils.handle(e);
        }

        try {
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
            initReadOnlyMulti();
            MultiReddit multi = manager.get(readOnlyMulti);
            ThingFieldTest.fieldValidityCheck(multi);

            TestUtils.testRenderString(manager.getDescription(account.getFullName(), readOnlyMulti));
        } catch (NetworkException | ApiException e) {
            TestUtils.handle(e);
        }
    }

    @Test
    public void testDescription() {
        try {
            initReadOnlyMulti();
            RenderStringPair desc = manager.getDescription(readOnlyMulti);
            TestUtils.testRenderString(desc);
        } catch (NetworkException | ApiException e) {
            e.printStackTrace();
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

    private static void initReadOnlyMulti() throws NetworkException, ApiException {
        if (readOnlyMulti != null) return;

        List<MultiReddit> mine = manager.mine();
        // Get the name of a multireddit that is NOT the one that will be created/deleted (MULTI_NAME)
        String name = null;
        for (MultiReddit multi : mine) {
            if (!multi.getFullName().equals(MULTI_NAME)) {
                name = multi.getFullName();
                break;
            }
        }

        if (name == null) {
            throw new IllegalStateException("You must create a multireddit. See https://github.com/thatJavaNerd/JRAW#contributing");
        }

        readOnlyMulti = name;
    }
}
