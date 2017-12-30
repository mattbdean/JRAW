package net.dean.jraw.docs.samples;

import net.dean.jraw.RedditClient;
import net.dean.jraw.docs.CodeSample;
import net.dean.jraw.models.*;
import net.dean.jraw.pagination.BarebonesPaginator;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.pagination.Paginator;
import net.dean.jraw.references.InboxReference;
import net.dean.jraw.references.MultiredditReference;
import net.dean.jraw.tree.CommentNode;
import net.dean.jraw.tree.RootCommentNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
final class Cookbook {
    RedditClient redditClient;

    @CodeSample
    void iterateFrontPage() {
        // frontPage() returns a Paginator.Builder
        DefaultPaginator<Submission> frontPage = redditClient.frontPage()
            .sorting(SubredditSort.TOP)
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
    void imagesFromMultipleSubreddits() {
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
    void userSubscriptions() {
        // Make sure we have a logged-in user or this call will fail!
        BarebonesPaginator<Subreddit> paginator = redditClient.me().subreddits("subscriber")
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
    void createAndUpdateMultireddit() {
        MultiredditPatch multiSpec = new MultiredditPatch.Builder()
            .description("Here's a cool multireddit!")
            .subreddits("redditdev", "kotlin", "java")
            .visibility("private")
            .build();
        Multireddit multi = redditClient.me().createMulti("my_multireddit", multiSpec);

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
    void iterateSpecificMultireddit() {
        // reddit staff members have subreddits for their pets, the multireddit at
        // "/user/reddit/m/redditpets" contains them all
        MultiredditReference ref = redditClient.user("reddit").multi("redditpets");

        // Get the first page
        Listing<Submission> posts = ref.posts().build().next();
    }

    @CodeSample
    void listMultireddits() {
        List<Multireddit> mine = redditClient.me().listMultis();
        List<Multireddit> someoneElses = redditClient.user("reddit").listMultis();
    }

    @CodeSample
    void iterateInbox() {
        BarebonesPaginator<Message> unread = redditClient.me().inbox().iterate("unread")
            .build();

        Listing<Message> firstPage = unread.next();
    }

    @CodeSample
    void markReadUnread() {
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
    void sendPrivateMessage() {
        InboxReference inbox = redditClient.me().inbox();
        inbox.compose("thatJavaNerd", "receiver username", "body");
    }

    @CodeSample
    void sendPrivateMessageAsModerator() {
        // Make sure to exclude the "/r/" prefix
        redditClient.me().inbox().compose("some_subreddit_i_moderate", "receiver username",
            "subject", "body");
    }

    @CodeSample
    void basicInfo() {
        Account me = redditClient.me().about();
        Account someoneElse = redditClient.user("Shitty_Watercolour").about();
    }

    @CodeSample
    void trophies() {
        List<Trophy> mine = redditClient.me().trophies();
        List<Trophy> someoneElses = redditClient.user("Shitty_Watercolour").trophies();
    }

    @CodeSample
    void setAndRemoveFlair() {
        List<Flair> userFlairOptions = redditClient.subreddit("RocketLeague").userFlairOptions();
        redditClient.subreddit("RocketLeague")
            .selfUserFlair()
            .updateToTemplate(userFlairOptions.get(0).getId(), "");
    }

    @CodeSample
    void traverseCommentTree() {
        // Request the comments of some submission. comments() takes more parameters where you
        // can customize things like comment sorting and only returning a specific comment
        // thread.
        RootCommentNode root = redditClient.submission("92dd8").comments();

        // walkTree() returns a Kotlin Sequence. Since we're using Java, we're going to have to
        // turn it into an Iterator to get any use out of it.
        Iterator<CommentNode<PublicContribution<?>>> it = root.walkTree().iterator();

        while (it.hasNext()) {
            // A PublicContribution is either a Submission or a Comment.
            PublicContribution<?> thing = it.next().getSubject();

            // Do something with each Submission/Comment
            System.out.println(thing.getBody());
        }
    }
}
