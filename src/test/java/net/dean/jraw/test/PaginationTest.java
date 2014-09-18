package net.dean.jraw.test;

import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Submission;
import net.dean.jraw.models.core.Thing;
import net.dean.jraw.pagination.*;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class PaginationTest {
    private static RedditClient reddit;
    private static LoggedInAccount account;

    @BeforeClass
    public static void setUp() throws NetworkException, ApiException {
        reddit = TestUtils.client(PaginationTest.class);
        String[] creds = TestUtils.getCredentials();
        account = reddit.login(creds[0], creds[1]);
    }

    @Test
    public void testFrontPage() throws NetworkException {
        SubredditPaginator frontPage = new SubredditPaginator(reddit);
        commonTest(frontPage);
    }

    @Test
    public void testSubreddit() throws NetworkException {
        SubredditPaginator pics = new SubredditPaginator(reddit, "pics");
        commonTest(pics);
    }

    @Test
    public void testSearch() throws NetworkException {
        SearchPaginator paginator = new SearchPaginator(reddit, "test");
        String subreddit = "AskReddit";
        paginator.setSubreddit(subreddit);
        commonTest(paginator);

        for (Submission s : paginator.getCurrentListing()) {
            // Make sure they all come from the same subreddit
            Assert.assertTrue(s.getSubredditName().equals(subreddit));
        }
    }

    @Test
    public void testUserContributions() throws NetworkException {
        // Test all Where values
        for (UserContributionPaginator.Where where : UserContributionPaginator.Where.values()) {
            UserContributionPaginator paginator = new UserContributionPaginator(reddit, where, account.getFullName());
            commonTest(paginator);
        }
    }

    @Test
    public void testById() throws NetworkException {
        // It would be easier to declare fullNames as an array, but we want to use List.contains()
        List<String> fullNames = Arrays.asList("t3_92dd8", "t3_290287", "t3_28zy98", "t3_28zh9i");
        SpecificPaginator paginator = new SpecificPaginator(reddit, fullNames.toArray(new String[fullNames.size()]));

        Listing<Submission> submissions = paginator.next();
        for (Submission s : submissions) {
            Assert.assertTrue(fullNames.contains(s.getFullName()));
        }
    }

    @Test(timeOut = 15_000)
    public void testPaginationTerminates() throws NetworkException {
        UserContributionPaginator paginator = new UserContributionPaginator(reddit, UserContributionPaginator.Where.SUBMITTED,
                TestUtils.getCredentials()[0]);

        while (paginator.hasNext()) {
            paginator.next();
        }
    }

    @Test
    public void testMySubredditsPaginator() throws NetworkException {
        // Test all Where values
        for (MySubredditsPaginator.Where where : MySubredditsPaginator.Where.values()) {
            MySubredditsPaginator paginator = new MySubredditsPaginator(account, where);
            commonTest(paginator);
        }
    }


    @Test
    public void testAllSubredditsPaginator() throws NetworkException {
        // Test all Where values
        for (AllSubredditsPaginator.Where where : AllSubredditsPaginator.Where.values()) {
            AllSubredditsPaginator paginator = new AllSubredditsPaginator(reddit, where);
            commonTest(paginator);
        }
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testChangeRequestParamters() {
        AllSubredditsPaginator paginator = new AllSubredditsPaginator(reddit, AllSubredditsPaginator.Where.NEW);
        paginator.next();
        // Modifying the request parameters after the initial request
        paginator.setLimit(Paginator.DEFAULT_LIMIT);
        // Should throw an IllegalStateException
        paginator.next();
    }

    @Test
    public void testResetRequestParameters() {
        AllSubredditsPaginator paginator = new AllSubredditsPaginator(reddit, AllSubredditsPaginator.Where.NEW);
        paginator.next();
        paginator.setLimit(Paginator.DEFAULT_LIMIT);
        // We know it has already started, but just making sure this method works as expected
        if (paginator.hasStarted()) {
            paginator.reset();
        }

        // Now should not throw an IllegalStateException
        paginator.next();
    }


    private <T extends Thing> void commonTest(Paginator<T> p) throws NetworkException {
        // Test that the paginator can retrieve the data
        Listing<T> firstPage = p.next();
        ThingFieldTest.fieldValidityCheck(firstPage);

        if (firstPage.size() > 0) {
            // Normal Thing
            ThingFieldTest.fieldValidityCheck(firstPage.get(0));
        }
    }
}
