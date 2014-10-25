package net.dean.jraw.test.auth;

import com.google.common.collect.ImmutableList;
import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.managers.MultiRedditManager;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.MultiReddit;
import net.dean.jraw.models.RenderStringPair;
import net.dean.jraw.paginators.MultiHubPaginator;
import net.dean.jraw.paginators.Paginator;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * This class tests the {@link MultiRedditManager} class.
 */
public class MultiRedditTest extends AuthenticatedRedditTest {
    private static final String MULTI_NAME = "jraw_testing";
    private static final List<String> MULTI_INITIAL_SUBS = ImmutableList.<String>builder()
                                                                    .add("funny", "pics")
                                                                    .build();
    private static final String DESC1 = "description 1";
    private static final String DESC2 = "description 2";

    private MultiRedditManager manager;
    private static String readOnlyMulti;

    public MultiRedditTest() {
        manager = new MultiRedditManager(reddit);
    }

    @BeforeClass
    public void setUp() {
        try {
            manager.update(MULTI_NAME, MULTI_INITIAL_SUBS, true);

            // Initialize the read-only multireddit name
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
        } catch (ApiException e) {
            if (!e.getReason().equals("MULTI_EXISTS")) {
                // https://github.com/thatJavaNerd/JRAW/issues/7
                handle(e);
            }
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @AfterTest
    public void reset() {
        try {
            manager.update(MULTI_NAME, MULTI_INITIAL_SUBS, true);
        } catch (NetworkException | ApiException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public void deleteMulti() {
        try {
            manager.delete(MULTI_NAME);
        } catch (NetworkException e) {
            if (e.getCode() != 404) {
                JrawUtils.logger().warn("Could not delete the testing multireddit (" + MULTI_NAME + ")");
            }
        }
    }

    @Test
    public void testMyMultis() {
        try {
            List<MultiReddit> multis = manager.mine();
            validateModels(multis);
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    @Test
    public void testCreate() {
        try {
            // Delete if exists
            if (multiExists(MULTI_NAME)) {
                manager.delete(MULTI_NAME);
            }

            manager.create(MULTI_NAME, MULTI_INITIAL_SUBS, true);

            assertTrue(multiExists(MULTI_NAME));
        } catch (ApiException e) {
            if (!e.getReason().equals("MULTI_EXISTS")) {
                // https://github.com/thatJavaNerd/JRAW/issues/7
                handle(e);
            }
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test(dependsOnMethods = "testCreate")
    public void testUpdate() {
        try {
            List<String> updatedSubreddits = new ArrayList<>(MULTI_INITIAL_SUBS);
            updatedSubreddits.add("programming");
            updatedSubreddits.add("java");

            manager.update(MULTI_NAME, updatedSubreddits, true);

            MultiReddit multi = manager.get(MULTI_NAME);
            compareLists(multi.getSubreddits(), updatedSubreddits);
        } catch (ApiException | NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testAddSubreddit() {
        try {
            String newSubreddit = "programming";
            MultiReddit beforeAddition = manager.get(MULTI_NAME);

            List<String> expectedSubreddits = new ArrayList<>(beforeAddition.getSubreddits());

            // Remove the subreddit from the multi if it is already included
            if (beforeAddition.getSubreddits().contains(newSubreddit)) {
                manager.removeSubreddit(MULTI_NAME, newSubreddit);
            } else {
                expectedSubreddits.add(newSubreddit);
            }

            // Add the subreddit to the multi
            manager.addSubreddit(MULTI_NAME, newSubreddit);

            MultiReddit afterAddition = manager.get(MULTI_NAME);

            // Make sure the addition took hold
            compareLists(afterAddition.getSubreddits(), expectedSubreddits);
        } catch (ApiException | NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testRemoveSubreddit() {
        try {
            MultiReddit beforeRemoval = manager.get(MULTI_NAME);

            List<String> expectedSubreddits = new ArrayList<>(beforeRemoval.getSubreddits());
            String oldSubreddit = expectedSubreddits.remove(0);

            manager.removeSubreddit(MULTI_NAME, oldSubreddit);

            MultiReddit afterRemoval = manager.get(MULTI_NAME);

            compareLists(afterRemoval.getSubreddits(), expectedSubreddits);
        } catch (ApiException | NetworkException e) {
            handle(e);
        }
    }

    private <T> void compareLists(List<T> actual, List<T> expected) {
        assertEquals(actual.size(), expected.size());
        for (T object : actual) {
            assertTrue(expected.contains(object));
        }
    }

    @Test
    public void testDelete() {
        try {
            // Actually test the method
            manager.delete(MULTI_NAME);

            assertNull(getMulti(MULTI_NAME));
        } catch (ApiException | NetworkException e) {
            handle(e);
        } finally {
            try {
                manager.update(MULTI_NAME, MULTI_INITIAL_SUBS, true);
            } catch (NetworkException | ApiException e) {
                JrawUtils.logger().warn("Could not re-create the deleted multireddit", e);
            }
        }
    }

    @Test
    public void testMulti() {
        try {
            MultiReddit multi = manager.get(readOnlyMulti);
            validateModel(multi);

            validateRenderString(manager.getDescription(reddit.getAuthenticatedUser(), readOnlyMulti));
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    @Test
    public void testUnownedMulti() { // aka public multis
        MultiHubPaginator multihub = new MultiHubPaginator(reddit);
        multihub.setLimit(Paginator.RECOMMENDED_MAX_LIMIT);
        final int amount = 3; // Amount of multireddits to find and test
        final int maxPages = 3; // Maximum amount of pages to look through
        List<MultiReddit> multiReddits = new ArrayList<>(amount);

        boolean fulfilled = false;
        // While we have less than 3 multireddits and haven't gone past page 3
        while (multihub.getPageIndex() <= maxPages && !fulfilled) {
            for (MultiHubPaginator.MultiRedditId id : multihub.next()) {
                try {
                    multiReddits.add(manager.get(id.getOwner(), id.getName()));
                    if (multiReddits.size() >= amount) {
                        fulfilled = true;
                        break;
                    }
                } catch (NetworkException e) {
                    if (e.getCode() != 404) {
                        JrawUtils.logger().info("Got 404, multi was deleted or renamed");
                        handle(e);
                    }

                    // Got 404 Not Found, that multi was renamed or deleted, continue on
                } catch (ApiException e) {
                    handle(e);
                }
            }
        }

        // We should have been able to find 3 multireddit links searching through 75 entries...
        assertTrue(multiReddits.size() == amount);

        // Test each MultiReddit
        validateModels(multiReddits);
    }

    @Test
    public void testCopy() {
        String newName = MULTI_NAME + "_copy";
        try {
            manager.copy(reddit.getAuthenticatedUser(), MULTI_NAME, newName);

            MultiReddit original = getMulti(MULTI_NAME);
            MultiReddit copied = getMulti(newName);

            assertTrue(copied.getFullName().equals(newName));
            assertNotNull(getMulti(newName));
            assertEquals(copied, original);
        } catch (NetworkException | ApiException e) {
            handle(e);
        } finally {
            try {
                manager.delete(newName);
            } catch (NetworkException e) {
                JrawUtils.logger().warn("Unable to delete " + newName, e);
            }
        }
    }

    @Test
    public void testRename() {
        String newName = MULTI_NAME + "_after";

        try {
            manager.rename(MULTI_NAME, newName);
            assertNotNull(getMulti(newName));
            assertNull(getMulti(MULTI_NAME));
        } catch (NetworkException | ApiException e) {
            handle(e);
        } finally {
            try {
                manager.rename(newName, MULTI_NAME);
            } catch (NetworkException | ApiException e) {
                JrawUtils.logger().warn("Could not return the multireddit to its original name");
                e.printStackTrace();
            }
        }

    }

    @Test
    public void testUpdateDescription() {
        try {
            RenderStringPair desc = manager.getDescription(MULTI_NAME);

            String expectedMd = desc.md().equals(DESC1) ? DESC2 : DESC1;
            manager.updateDescription(MULTI_NAME, expectedMd);

            RenderStringPair newDesc = manager.getDescription(MULTI_NAME);

            assertEquals(newDesc.md(), expectedMd);
        } catch (NetworkException | ApiException  e) {
            handle(e);
        }
    }

    @Test
    public void testGetDescription() {
        try {
            RenderStringPair desc = manager.getDescription(readOnlyMulti);
            validateRenderString(desc);
        } catch (NetworkException | ApiException e) {
            e.printStackTrace();
        }
    }

    private MultiReddit getMulti(String name) throws NetworkException, ApiException {
        for (MultiReddit mine : manager.mine()) {
            if (mine.getFullName().equals(name)) {
                return mine;
            }
        }

        return null;
    }

    private boolean multiExists(String name) throws NetworkException, ApiException {
        return getMulti(name) != null;
    }
}
