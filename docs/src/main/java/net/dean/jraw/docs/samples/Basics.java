package net.dean.jraw.docs.samples;

import net.dean.jraw.RedditClient;
import net.dean.jraw.docs.CodeSample;
import net.dean.jraw.models.Flair;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.references.SubredditReference;

import java.util.List;

final class Basics {
    @CodeSample
    void updateFlair(RedditClient redditClient) {
        // "Navigate" to the subreddit
        SubredditReference subreddit = redditClient.subreddit("RocketLeague");

        // Request available user flair
        List<Flair> userFlairOptions = subreddit.userFlairOptions();

        if (!userFlairOptions.isEmpty()) {
            // Arbitrarily choose a new Flair
            Flair newFlair = userFlairOptions.get(0);

            // Update the flair on the website
            subreddit.selfUserFlair().updateTo(newFlair.getId());
        }
    }

    @CodeSample
    void referenceChain(RedditClient redditClient) {
        Subreddit sr = redditClient.subreddit("RocketLeague").about();
    }
}
