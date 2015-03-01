package net.dean.jraw.test;

import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.http.MultiRedditUpdateRequest;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.managers.MultiRedditManager;
import net.dean.jraw.models.MultiReddit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

/**
 * This class tests the {@link MultiRedditManager} class.
 */
public class MultiRedditTest extends RedditTest {
    private static final String MULTI_NAME = "jraw_testing";
    private static final List<String> INITIAL_SUBS = Arrays.asList("programming", "java", "git");

    private MultiRedditManager manager;
    private String testingMulti;

    public MultiRedditTest() {
        manager = new MultiRedditManager(reddit);
    }

    @BeforeMethod
    public void setUp() {
        try {
            manager.createOrUpdate(new MultiRedditUpdateRequest.Builder(reddit.getAuthenticatedUser(), MULTI_NAME)
                    .subreddits(INITIAL_SUBS)
                    .visibility(MultiReddit.Visibility.PRIVATE)
                    .build());
        } catch (NetworkException | ApiException e) {
            JrawUtils.logger().warn("Could not set up the test", e);
        }
    }

    @Test
    public void testMine() {
        try {
            validateModels(manager.mine());
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    @Test
    public void testGet() {
        try {
            validateModel(manager.get(getReadOnlyMulti().getFullName()));
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    @Test
    public void testUpdateDescription() {
        String desc1 = "desc1";
        String desc2 = "desc2";

        try {
            String before = manager.get(MULTI_NAME).getDescription();
            String expected = before.equals(desc1) ? desc2 : desc1;

            manager.updateDescription(MULTI_NAME, expected);

            assertEquals(manager.get(MULTI_NAME).getDescription(), expected);
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    @Test
    public void testDelete() {
        try {
            manager.delete(MULTI_NAME);
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testCreateOrUpdate() {
        try {
            manager.delete(MULTI_NAME);
        } catch (NetworkException e) {
            // We don't care if it 404's
            if (e.getResponse().getStatusCode() != 404) {
                handle(e);
            }
        }
        try {
            assertFalse(multiExists(MULTI_NAME));
            MultiReddit mr = manager.createOrUpdate(new MultiRedditUpdateRequest.Builder(reddit.getAuthenticatedUser(), MULTI_NAME)
                    .visibility(MultiReddit.Visibility.PRIVATE)
                    .description("test description")
                    .subreddits(INITIAL_SUBS)
                    .displayName("test-display-name")
                    .icon(MultiReddit.Icon.ART_AND_DESIGN)
                    .keyColor("#FFFFFF")
                    .weightingScheme(MultiReddit.WeightingScheme.CLASSIC)
                    .build());
            validateModels(mr.getSubreddits());

            assertTrue(multiExists(MULTI_NAME));
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    @Test
    public void testRename() {
        String newName = MULTI_NAME + "_new";
        try {
            manager.rename(MULTI_NAME, newName);
            assertTrue(multiExists(newName));
        } catch (NetworkException | ApiException e) {
            handle(e);
        } finally {
            try {
                manager.delete(newName);
            } catch (NetworkException e) {
                JrawUtils.logger().warn("Could not delete multi that was renamed", e);
            }
        }
    }

    @Test
    public void testCopy() {
        String newName = MULTI_NAME + "_copy";
        try {
            manager.delete(newName);
        } catch (NetworkException e) {
            // We don't care if it 404's
            if (e.getResponse().getStatusCode() != 404) {
                handle(e);
            }
        }

        try {
            manager.copy(MULTI_NAME, newName);
            assertTrue(multiExists(newName));
        } catch (NetworkException | ApiException e) {
            handle(e);
        } finally {
            try {
                manager.delete(newName);
            } catch (NetworkException e) {
                JrawUtils.logger().warn("Could not delete multi that was renamed", e);
            }
        }
    }

    @Test
    public void testAddSubreddit() {
        try {
            manager.addSubreddit(MULTI_NAME, "funny");
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testRemoveSubreddit() {
        try {
            manager.removeSubreddit(MULTI_NAME, INITIAL_SUBS.get(0));
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testGetPublicMultis() {
        try {
            validateModels(manager.getPublicMultis(reddit.getAuthenticatedUser()));
        } catch (NetworkException e) {
            handle(e);
        }
    }

    private boolean multiExists(String name) {
        try {
            manager.get(name);
            return true;
        } catch (NetworkException | ApiException e) {
            return false;
        }
    }

    private MultiReddit getReadOnlyMulti() {
        try {
            if (testingMulti != null) {
                return manager.get(testingMulti);
            }

            for (MultiReddit m : manager.mine()) {
                if (!m.getFullName().equals(MULTI_NAME)) {
                    this.testingMulti = m.getFullName();
                    return m;
                }
            }
        } catch (NetworkException | ApiException e) {
            handle(e);
        }

        throw new SetupRequiredException("Create a multireddit NOT named " + MULTI_NAME);
    }
}
