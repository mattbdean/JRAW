package net.dean.jraw.docs.samples;

import net.dean.jraw.RedditClient;
import net.dean.jraw.docs.CodeSample;
import net.dean.jraw.models.*;
import net.dean.jraw.pagination.BarebonesPaginator;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.pagination.Paginator;
import net.dean.jraw.references.InboxReference;
import net.dean.jraw.references.MultiredditReference;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Cookbook {
    private static RedditClient redditClient;

    @CodeSample
    private static void iterateFrontPage() {
        // frontPage() returns a Paginator.Builder
        DefaultPaginator<Submission> frontPage = redditClient.frontPage()
            .sorting(Sorting.TOP)
            .timePeriod(TimePeriod.DAY)
            .limit(30)
            .build();

        Listing<Submission> submissions = frontPage.next();
        for (Submission s : submissions) {
            System.out.println(s.getTitle());
        }
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @CodeSample
    private static void imagesFromMultipleSubreddits() {
        DefaultPaginator<Submission> earthPorn = redditClient.subreddits("EarthPorn", "spaceporn").build();

        List<String> images = new ArrayList<String>();
        for (Submission s : earthPorn.next()) {
            if (!s.isSelfPost() && s.getUrl().contains("i.imgur.com")) {
                images.add(s.getUrl());
            }
        }

        // do something with `images`
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @CodeSample
    private static void userSubscriptions() {
        // Make sure we have a logged-in user or this call will fail!
        DefaultPaginator<Subreddit> paginator = redditClient.me().subreddits("subscriber")
            // Request as many items as possible
            .limit(Paginator.RECOMMENDED_MAX_LIMIT)
            .build();

        List<Subreddit> subscribed = new ArrayList<Subreddit>();

        // Paginator implements Iterable, so we can use the enhanced for loop to iterate the Paginator until reddit
        // can't give us anything else. Don't do this for posts on a subreddit or the front page!
        for (Listing<Subreddit> page : paginator) {
            subscribed.addAll(page);
        }

        // Do something with `subscribed`
    }

    @SuppressWarnings("UnusedAssignment")
    @CodeSample
    private static void createAndUpdateMultireddit() {
        Multireddit multi = redditClient.me().createMulti("my_multireddit", new MultiredditPatch.Builder()
            .description("Here's a cool multireddit!")
            .subreddits("redditdev", "kotlin", "java")
            .visibility("private")
            .build());

        // Turn the model class into a Reference, which we can use to interact with the API
        MultiredditReference ref = multi.toReference(redditClient);

        // Add or remove subreddits one at a time
        ref.addSubreddit("programming");
        ref.removeSubreddit("java");

        // Or we can change things in bulk
        multi = ref.update(new MultiredditPatch.Builder()
            .subreddits("redditdev")
            .build());
    }

    @CodeSample
    private static void iterateSpecificMultireddit() {
        // reddit staff members have subreddits for their pets, the multireddit at "/user/reddit/m/redditpets" contains
        // them all
        MultiredditReference ref = redditClient.user("reddit").multi("redditpets");

        // Get the first page
        Listing<Submission> posts = ref.posts().build().next();
    }

    @CodeSample
    private static void listMultireddits() {
        List<Multireddit> mine = redditClient.me().listMultis();
        List<Multireddit> someoneElses = redditClient.user("reddit").listMultis();
    }

    @CodeSample
    private static void iterateInbox() {
        BarebonesPaginator<Message> unread = redditClient.me().inbox().iterate("unread")
            .build();

        Listing<Message> firstPage = unread.next();
    }

    @CodeSample
    private static void markReadUnread() {
        // Get the first unread message in the inbox
        Listing<Message> unread = redditClient.me().inbox().iterate("unread")
            .limit(1)
            .build().next();

        if (!unread.isEmpty()) {
            Message first = unread.get(0);

            // The first message
            redditClient.me().inbox().markRead(true, first.getFullName());

        }
    }

    @CodeSample
    private static void sendPrivateMessage() {
        InboxReference inbox = redditClient.me().inbox();
        inbox.compose("thatJavaNerd", "receiver username", "body");
    }

    @CodeSample
    private static void sendPrivateMessageAsModerator() {
        // Make sure to exclude the "/r/" prefix
        redditClient.me().inbox().compose("some_subreddit_i_moderate", "receiver username", "subject", "body");
    }
}
