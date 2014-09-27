package net.dean.jraw.test.auth;

import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.MultiRedditManager;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.MultiReddit;
import net.dean.jraw.models.RenderStringPair;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Submission;
import net.dean.jraw.pagination.SubredditPaginator;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * This class tests the {@link MultiRedditManager} class.
 */
public class MultiRedditTest extends AuthenticatedRedditTest {
    private static final String MULTI_NAME = "jraw_testing";
    private static final List<String> MULTI_INITIAL_SUBS = Arrays.asList("funny", "pics");

    private MultiRedditManager manager;
    private static String readOnlyMulti;

    public MultiRedditTest() {
        manager = new MultiRedditManager(account);
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
                JrawUtils.logger().info("Deleting existing multi");
                manager.delete(MULTI_NAME);
            }

            manager.create(MULTI_NAME, MULTI_INITIAL_SUBS, true);

            assertTrue(multiExists(MULTI_NAME));
        } catch (ApiException e) {
            if (!e.getCode().equals("MULTI_EXISTS")) {
                // https://github.com/thatJavaNerd/JRAW/issues/7
                handle(e);
            }
        } catch (NetworkException e) {
            handle(e);
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
                handle(e);
            }
        } catch (NetworkException e) {
            handle(e);
        }

        try {
            // Actually test the method
            manager.delete(MULTI_NAME);

            assertFalse(multiExists(MULTI_NAME));
        } catch (ApiException | NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testMulti() {
        try {
            initReadOnlyMulti();
            MultiReddit multi = manager.get(readOnlyMulti);
            validateModel(multi);

            validateRenderString(manager.getDescription(account.getFullName(), readOnlyMulti));
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    @Test
    public void testUnownedMulti() { // aka public multis
        // Matches a multireddit URL: see http://regexr.com/39j27
        // Usernames must only alphanumeric characters, underscores, and hyphens allowed in usernames with a max length of 20
        // Source: https://github.com/reddit/reddit/blob/c86113850/r2/r2/lib/validator/validator.py#L1311
        // Multireddits must only contain alphanumeric characters, underscores, and hyphens, and have a max length of 21
        // Source: https://github.com/reddit/reddit/blob/3b7b74148/r2/r2/lib/validator/validator.py#L2622
        Pattern multiRedditUrl = Pattern.compile("reddit\\.com/user/([a-zA-Z\\-_]*?)/m/([A-Za-z0-9][A-Za-z0-9_]{1,20})");
        Matcher matcher = multiRedditUrl.matcher("");

        // Parse mutlireddits from /r/multihub
        SubredditPaginator multihub = new SubredditPaginator(reddit, "multihub");
        final int amount = 3; // Amount of multireddits to find and test
        final int maxPages = 3; // Maximum amount of pages to look through
        List<MultiReddit> multiReddits = new ArrayList<>(amount);

        boolean fulfilled = false;
        // While we have less than 3 multireddits and haven't gone past page 3
        while (multihub.getPageIndex() <= maxPages && !fulfilled) {
            Listing<Submission> submissions = multihub.next();
            for (Submission potentialMultiLink : submissions) {
                if (multiReddits.size() >= amount) {
                    fulfilled = true;
                    break;
                }
                matcher.reset(potentialMultiLink.getUrl().toExternalForm());
                if (matcher.find(1) && matcher.find(2)) {
                    // Found username and multi name
                    try {
                        multiReddits.add(manager.get(matcher.group(1), matcher.group(2)));
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
        }

        // We should have been able to find 3 multireddit links searching through 75 pages...
        assertTrue(multiReddits.size() == amount);

        // Test each MultiReddit
        validateModels(multiReddits);
    }

    @Test
    public void testDescription() {
        try {
            initReadOnlyMulti();
            RenderStringPair desc = manager.getDescription(readOnlyMulti);
            validateRenderString(desc);
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

    private void initReadOnlyMulti() throws NetworkException, ApiException {
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
