package net.dean.jraw.test.auth;

import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.managers.MultiRedditManager;
import net.dean.jraw.models.MultiReddit;
import net.dean.jraw.models.RenderStringPair;
import net.dean.jraw.test.SetupRequiredException;import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * This class tests the {@link MultiRedditManager} class.
 */
public class MultiRedditTest extends AuthenticatedRedditTest {
    private static final String MULTI_NAME = "jraw_testing";
    private static final List<String> INITIAL_MULTIS = Arrays.asList("programming", "java", "git");

    private MultiRedditManager manager;
    private String testingMulti;

    public MultiRedditTest() {
        manager = new MultiRedditManager(reddit);
    }

    @BeforeMethod
    public void setUp() {
        try {
            manager.createOrUpdate(MULTI_NAME, INITIAL_MULTIS, false);
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
            e.printStackTrace();
        }
    }

    @Test
    public void testGetDescription() {
        try {
            validateRenderString(manager.getDescription(manager.mine().get(0).getFullName()));
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    @Test
    public void testUpdateDescription() {
        String desc1 = "desc1";
        String desc2 = "desc2";

        try {
            RenderStringPair before = manager.getDescription(MULTI_NAME);
            String expected = before.md().equals(desc1) ? desc2 : desc1;

            manager.updateDescription(MULTI_NAME, expected);

            assertEquals(manager.getDescription(MULTI_NAME).md(), expected);
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
        } catch (ApiException e) {
            if (!e.getReason().equals("MULTI_NOT_FOUND")) {
                handle(e);
            }
        }
    }

    @Test
    public void testCreateOrUpdate() {
        try {
            manager.delete(MULTI_NAME);
            assertFalse(multiExists(MULTI_NAME));
            validateModel(manager.createOrUpdate(MULTI_NAME, INITIAL_MULTIS, true));
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
            e.printStackTrace();
        } finally {
            try {
                manager.delete(newName);
            } catch (NetworkException | ApiException e) {
                JrawUtils.logger().warn("Could not delete multi that was renamed", e);
            }
        }
    }

    @Test
    public void testCopy() {
        String newName = MULTI_NAME + "_new";
        try {
            manager.copy(MULTI_NAME, newName);
            assertTrue(multiExists(newName));
        } catch (NetworkException | ApiException e) {
            handle(e);
        } finally {
            try {
                manager.delete(newName);
            } catch (NetworkException | ApiException e) {
                JrawUtils.logger().warn("Could not delete multi that was renamed", e);
            }
        }
    }

    @Test
    public void testAddSubreddit() {
        try {
            manager.addSubreddit(MULTI_NAME, "funny");
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    @Test
    public void testRemoveSubreddit() {
        try {
            manager.removeSubreddit(MULTI_NAME, INITIAL_MULTIS.get(0));
        } catch (NetworkException | ApiException e) {
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
