package net.dean.jraw.test;

import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.managers.MultiRedditManager;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.MultiReddit;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Thing;
import net.dean.jraw.paginators.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * This class tests all concrete subclasses of {@link net.dean.jraw.paginators.Paginator}
 */
public class PaginationTest extends RedditTest {
    private MultiRedditManager manager = new MultiRedditManager(reddit);

    @Test
    public void testSubredditPaginatorFrontPage() throws NetworkException {
        SubredditPaginator frontPage = Paginators.frontPage(reddit);
        commonTest(frontPage);
    }

    @Test
    public void testSubredditPaginatorSubreddit() throws NetworkException {
        SubredditPaginator pics = new SubredditPaginator(reddit, "pics");
        commonTest(pics);
    }

    @Test
    public void testPaginatorTimePeriod() {
        try {
            final long millisecondsInAnHour = 60 * 60 * 1000;

            SubredditPaginator frontPage = Paginators.frontPage(reddit);
            frontPage.setSorting(Sorting.TOP);
            frontPage.setTimePeriod(TimePeriod.HOUR);
            Listing<Submission> submissions = frontPage.next();

            for (Submission post : submissions) {
                long epochPosted = post.getCreatedUtc().getTime();
                long epochNow = new Date().getTime();

                // Make sure the submissions have been posted in the past hour
                assertTrue(epochPosted > epochNow - millisecondsInAnHour);
            }
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testSearchPaginator() throws NetworkException {
        SubmissionSearchPaginator paginator = Paginators.searchPosts(reddit, "test");
        String subreddit = "AskReddit";
        paginator.setSubreddit(subreddit);
        commonTest(paginator);

        for (Submission s : paginator.getCurrentListing()) {
            // Make sure they all come from the same subreddit
            Assert.assertTrue(s.getSubredditName().equals(subreddit));
        }
    }

    @Test
    public void testSpecificPaginator() throws NetworkException {
        // It would be easier to declare fullNames as an array, but we want to use List.contains()
        List<String> fullNames = Arrays.asList("t3_92dd8", "t3_290287", "t3_28zy98", "t3_28zh9i");
        SpecificPaginator paginator = Paginators.byId(reddit, fullNames.toArray(new String[fullNames.size()]));

        Listing<Submission> submissions = paginator.next();
        for (Submission s : submissions) {
            Assert.assertTrue(fullNames.contains(s.getFullName()));
        }
    }

    @Test
    public void testUserContributionPaginator() throws NetworkException {
        // Test all Where values
        String[] wheres = Paginators.contributions(reddit, "", "overview").getWhereValues();

        for (String where : wheres) {
            UserContributionPaginator paginator = Paginators.contributions(reddit, reddit.getAuthenticatedUser(), where);
            commonTest(paginator);
        }
    }

    @Test
    public void testUserSubredditsPaginator() throws NetworkException {
        String[] wheres = Paginators.mySubreddits(reddit, "subscriber").getWhereValues();
        // Test all Where values

        for (String where : wheres) {
            UserSubredditsPaginator paginator = Paginators.mySubreddits(reddit, where);
            commonTest(paginator);
        }
    }

    @Test
    public void testAllSubredditsPaginator() throws NetworkException {
        // Test all Where values
        for (String where : new String[] {"popular", "new"}) {
            AllSubredditsPaginator paginator = Paginators.allSubreddits(reddit, where);
            commonTest(paginator);
        }
    }

    @Test
    public void testMultiRedditPaginator() throws ApiException {
        MultiReddit multi = manager.mine().get(0);

        MultiRedditPaginator paginator = Paginators.multireddit(reddit, multi);
        commonTest(paginator);
    }

    @Test
    public void testCompoundSubredditPaginator() {
        SubredditPaginator paginator = Paginators.subreddit(reddit, "programming", "java");
        // Paginators.subreddit() should have detected the additional subreddit and returned a CompoundSubreddit instead
        // of a normal SubredditPaginator
        assertTrue(paginator.getClass().equals(CompoundSubredditPaginator.class));
        commonTest(paginator);
    }

    @Test
    public void testMultiHubPaginator() {
        try {
            MultiHubPaginator paginator = Paginators.multihub(reddit);

            final int threshold = 3;
            int valid = 0;
            int invalid = 0;

            Listing<MultiHubPaginator.MultiRedditId> ids = paginator.next();

            for (MultiHubPaginator.MultiRedditId id : ids) {
                try {
                    MultiReddit multi = manager.get(id.getOwner(), id.getName());
                    validateModel(multi);
                    valid++;
                } catch (NetworkException | ApiException e) {
                    invalid++;
                }

                if (valid >= threshold) {
                    // Test passed
                    break;
                }
                if (invalid >= threshold) {
                    // More than the acceptable amount failed, something is probably broken
                    fail("Failed to get " + threshold + " separate multireddits");
                }
            }
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testLiveThreadPaginator() {
        commonTest(Paginators.liveThread(reddit, "ts4r8m1g99ys"));
    }

    @Test
    public void testInboxPaginator() {
        String[] wheres = Paginators.inbox(reddit, "inbox").getWhereValues();
        for (String where : wheres) {
            InboxPaginator paginator = Paginators.inbox(reddit, where);
            commonTest(paginator);
        }
    }

    @Test
    public void testUserRecordPaginator() {
        String[] wheres = Paginators.modRecords(reddit, "", "banned").getWhereValues();
        String modOf = getModeratedSubreddit().getDisplayName();

        for (String where : wheres) {
            UserRecordPaginator paginator = Paginators.modRecords(reddit, modOf, where);
            commonTest(paginator);
        }
    }

    @Test
    public void testImportantUserPaginator() {
        RedditClient[] clientsToTest = {reddit, reddit};
        for (RedditClient client : clientsToTest) {
            for (String where : new String[] {"friends", "blocked"}) {
                ImportantUserPaginator paginator = Paginators.importantUsers(client, where);
                commonTest(paginator);
            }
        }
    }

    @Test
    public void testModeratorPaginator() {
        String modOf = getModeratedSubreddit().getDisplayName();
        String[] wheres = Paginators.moderator(reddit, "", "reports").getWhereValues();

        for (String where : wheres) {
            ModeratorPaginator paginator = Paginators.moderator(reddit, modOf, where);
            // Test no filters
            commonTest(paginator);

            // Test only comments
            paginator.reset();
            paginator.setIncludeComments(true);
            commonTest(paginator);

            // Test only submissions
            paginator.reset();
            paginator.setIncludeSubmissions(true);
            commonTest(paginator);

            // Test both comments and submissions
            paginator.reset();
            paginator.setIncludeComments(true);
            paginator.setIncludeSubmissions(true);
        }
    }

    @Test
    public void testSubredditSearchPaginator() {
        SubredditSearchPaginator paginator = Paginators.searchSubreddits(reddit, "programming");
        commonTest(paginator);
    }

    @Test
    public void testDuplicatesPaginator() {
        commonTest(Paginators.duplicates(reddit, reddit.getSubmission("92dd8")));
    }

    @Test
    public void testRelatedPaginator() {
        commonTest(Paginators.related(reddit, reddit.getSubmission("92dd8")));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testChangeRequestParameters() {
        AllSubredditsPaginator paginator = Paginators.allSubreddits(reddit, "new");
        paginator.next();
        // Modifying the request parameters after the initial request, without calling reset
        paginator.setLimit(Paginator.DEFAULT_LIMIT);
        // Should throw an IllegalStateException
        paginator.next();
    }

    @Test
    public void testResetRequestParameters() {
        AllSubredditsPaginator paginator = Paginators.allSubreddits(reddit, "new");
        paginator.next();
        paginator.setLimit(Paginator.DEFAULT_LIMIT);
        // We know it has already started, but just making sure this method works as expected
        if (paginator.hasStarted()) {
            paginator.reset();
        }

        // Should not throw an IllegalStateException
        paginator.next();
    }

    @Test
    public void testAccumulateMerged() {
        Paginator<Submission> p = Paginators.frontPage(reddit);
        try {
            List<Submission> things = p.accumulateMerged(3);
            for (Submission s : things) {
                validateModel(s);
            }
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testModLog() {
        ModLogPaginator paginator = Paginators.modlog(reddit, getModeratedSubreddit().getDisplayName());
        commonTest(paginator);
    }

    protected <T extends Thing> void commonTest(Paginator<T> p) {
        int numPages = 2;
        // Test that the paginator can retrieve the data
        List<Listing<T>> pages = p.accumulate(numPages);

        for (Listing<T> listing : pages) {
            // Validate the Listing (not its children)
            validateModel(listing);

            if (listing.size() > 0) {
                // Validate Listing children
                validateModels(listing);
            } else {
                JrawUtils.logger().warn("Listing was empty");
            }
        }
    }
}
