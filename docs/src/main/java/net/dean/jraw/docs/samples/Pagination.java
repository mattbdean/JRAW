package net.dean.jraw.docs.samples;

import net.dean.jraw.RedditClient;
import net.dean.jraw.docs.CodeSample;
import net.dean.jraw.models.*;
import net.dean.jraw.pagination.BarebonesPaginator;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.pagination.Paginator;

import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
final class Pagination {
    private static RedditClient redditClient;

    @CodeSample
    void simple() {
        DefaultPaginator.Builder<Submission, SubredditSort> paginatorBuilder = redditClient.frontPage();
        DefaultPaginator<Submission> paginator = paginatorBuilder.build();

        Listing<Submission> firstPage = paginator.next();
        Listing<Submission> secondPage = paginator.next();
        // etc.
    }

    @CodeSample
    void usingAllOptions() {
        DefaultPaginator<Submission> paginator = redditClient.frontPage()
            .limit(50) // 50 posts per page
            .sorting(SubredditSort.TOP) // top posts
            .timePeriod(TimePeriod.ALL) // of all time
            .build();

        Listing<Submission> top50MostPopular = paginator.next();
    }

    @CodeSample
    void iterableWhile() {
        DefaultPaginator<Submission> paginator = redditClient.frontPage().build();

        Iterator<Listing<Submission>> it = paginator.iterator();

        while (it.hasNext()) {
            Listing<Submission> nextPage = it.next();
            // do something with nextPage
        }
    }

    @CodeSample
    void iterableForEach() {
        DefaultPaginator<Submission> paginator = redditClient.frontPage().build();

        for (Listing<Submission> nextPage : paginator) {
            // do something with nextPage
        }
    }

    @CodeSample
    void accumulate() {
        DefaultPaginator<Submission> paginator = redditClient
            .subreddit("pics")
            .posts()
            .build();

        // Get a maximum of three pages. Doesn't guarantee that there are three Listings here.
        List<Listing<Submission>> firstThreePages = paginator.accumulate(3);
    }

    @CodeSample
    void accumulateMerged() {
        BarebonesPaginator<Subreddit> paginator = redditClient
            .me()
            .subreddits("subscriber")
            // Send as few requests as possible by requesting as much data as possible
            .limit(Paginator.RECOMMENDED_MAX_LIMIT)
            .build();

        // Fetch all subscriptions and put them into a single list
        List<Subreddit> subscribedSubreddits = paginator.accumulateMerged(-1);
    }
}
