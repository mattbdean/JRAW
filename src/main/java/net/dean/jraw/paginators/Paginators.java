package net.dean.jraw.paginators;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.MultiReddit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class provides for the easy creation of Paginator objects
 */
public final class Paginators {
    /**
     * Creates an AllSubredditsPaginator
     *
     * @param reddit The RedditClient to send requests with
     * @param where What to look up. Either "new" or "popular"
     * @return A new AllSubredditsPaginator
     */
    public static AllSubredditsPaginator allSubreddits(RedditClient reddit, String where) {
        return new AllSubredditsPaginator(reddit, where);
    }

    /**
     * Creates a new SubredditPaginator that iterates through the front page
     * @param reddit The RedditClient to send requests with
     * @return A new SubredditPaginator
     */
    public static SubredditPaginator frontPage(RedditClient reddit) {
        return new SubredditPaginator(reddit);
    }

    /**
     * Creates a new SubredditPaginator that iterates through one or more subreddit. If there are more than one
     * subreddits specified, a {@link CompoundSubredditPaginator} will be returned.
     * @param reddit The RedditClient to send requests with
     * @param subreddit The subreddit to iterate
     * @param others Other subreddits to include in the request
     * @return A new SubredditPaginator
     */
    public static SubredditPaginator subreddit(RedditClient reddit, String subreddit, String... others) {
        if (others.length > 0) {
            List<String> subs = new ArrayList<>(others.length + 1);
            subs.add(subreddit);
            Collections.addAll(subs, others);
            return new CompoundSubredditPaginator(reddit, subs);
        }

        return new SubredditPaginator(reddit, subreddit);
    }

    /**
     * Creates a new ImportantUserPaginator
     * @param reddit The RedditClient to send requests with
     * @param where What to look up. Either "friends" or "blocked".
     * @return A new ImportantUserPaginator
     */
    public static ImportantUserPaginator importantUsers(RedditClient reddit, String where) {
        return new ImportantUserPaginator(reddit, where);
    }

    /**
     * Creates a new InboxPaginator
     * @param reddit The RedditClient to send requests with
     * @param where What to look up. One of "inbox", "unread", "messages", "sent", "moderator", or "moderator/unread".
     * @return A new InboxPaginator
     */
    public static InboxPaginator inbox(RedditClient reddit, String where) {
        return new InboxPaginator(reddit, where);
    }

    /**
     * Creates a new LiveThreadPaginator
     * @param reddit The RedditClient to send requests with
     * @param threadId The live thread's ID
     * @return A new LiveThreadPaginator
     */
    public static LiveThreadPaginator liveThread(RedditClient reddit, String threadId) {
        return new LiveThreadPaginator(reddit, threadId);
    }

    /**
     * Creates a new ModeratorPaginator
     * @param reddit The RedditClient to send requests with
     * @param subreddit The subreddit whose moderator-relevant links and comments will be observed
     * @param where What to look up. One of "reports", "spam", "modqueue", "unmoderated", or "edited".
     * @return A new ModeratorPaginator
     */
    public static ModeratorPaginator moderator(RedditClient reddit, String subreddit, String where) {
        return new ModeratorPaginator(reddit, subreddit, where);
    }

    /**
     * Creates a new ModLogPaginator
     * @param reddit The RedditClient to send requests with
     * @param subreddit The subreddit whose moderator actions will be observed
     * @return A new ModLogPaginator
     */
    public static ModLogPaginator modlog(RedditClient reddit, String subreddit) {
        return new ModLogPaginator(reddit, subreddit);
    }

    /**
     * Creates a new MultiHubPaginator, not to be confused with MultiRedditPaginator
     * @param reddit The RedditClient to send requests with
     * @return A new MultiHubPaginator.
     */
    public static MultiHubPaginator multihub(RedditClient reddit) {
        return new MultiHubPaginator(reddit);
    }

    /**
     * Creates a new MultiRedditPaginator
     * @param reddit The RedditClient to send requests with
     * @param multi The MultiReddit to iterate
     * @return A new MutliRedditPaginator
     */
    public static MultiRedditPaginator multireddit(RedditClient reddit, MultiReddit multi) {
        return new MultiRedditPaginator(reddit, multi);
    }

    /**
     * Creates a new SearchPaginator
     * @param reddit The RedditClient to send requests with
     * @param query What to search for
     * @return A new SearchPaginator
     */
    public static SubmissionSearchPaginator searchPosts(RedditClient reddit, String query) {
        return new SubmissionSearchPaginator(reddit, query);
    }

    /**
     * Creates a new SpecificPaginator
     * @param reddit The RedditClient to send requests with
     * @param fullNames An array of full names of submissions
     * @return A SpecificPaginator
     */
    public static SpecificPaginator byId(RedditClient reddit, String... fullNames) {
        return new SpecificPaginator(reddit, fullNames);
    }

    /**
     * Creates a new SubredditSearchPaginator
     * @param reddit The RedditClient to send requests with
     * @param query What to search for
     * @return A new SubredditSearchPaginator
     */
    public static SubredditSearchPaginator searchSubreddits(RedditClient reddit, String query) {
        return new SubredditSearchPaginator(reddit, query);
    }

    /**
     * Creates a new UserContributionPaginator
     * @param reddit The RedditClient to send requests with
     * @param username The user to look up
     * @param where What to look up. One of "overview", "gilded", "submitted", "liked", "disliked", "hidden", "saved",
     *              or "comments".
     * @return A new UserContributionPaginator
     */
    public static UserContributionPaginator contributions(RedditClient reddit, String username, String where) {
        return new UserContributionPaginator(reddit, where, username);
    }

    /**
     * Creates a new UserRecordPaginator
     * @param reddit The RedditClient to send requests with
     * @param subreddit The subreddit whose moderator-relevant users will be observed
     * @param where What to look up. One of "banned", "wikibanned", "contributors", "wikicontributors", or "moderators".
     * @return A new UserRecordPaginator
     */
    public static UserRecordPaginator modRecords(RedditClient reddit, String subreddit, String where) {
        return new UserRecordPaginator(reddit, subreddit, where);
    }

    /**
     * Creates a new UserSubredditsPaginator
     * @param reddit The RedditClient to send requests with
     * @param where What to look up. One of "subscriber", "contributor", or "moderator"
     * @return A new UserSubredditsPaginator
     */
    public static UserSubredditsPaginator mySubreddits(RedditClient reddit, String where) {
        return new UserSubredditsPaginator(reddit, where);
    }
}
