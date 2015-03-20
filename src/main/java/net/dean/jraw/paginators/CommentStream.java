package net.dean.jraw.paginators;

import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Comment;

/**
 * Provides a way to iterate through all of the newest comments in a given subreddit.
 */
public class CommentStream extends Paginator<Comment> {
    private String subreddit;

    /**
     * Instantiates a new CommentStream that will pull comments from any subreddit
     *
     * @param reddit The RedditClient that will be used to send HTTP requests
     */
    public CommentStream(RedditClient reddit) {
        this(reddit, null);
    }

    /**
     * Instantiates a new CommentStream
     *
     * @param reddit    The RedditClient that will be used to send HTTP requests
     * @param subreddit The subreddit to pull comments from
     */
    public CommentStream(RedditClient reddit, String subreddit) {
        super(reddit, Comment.class);
        this.subreddit = subreddit;
    }

    @Override
    protected String getBaseUri() {
        // "/r/{subreddit}/comments" or just "/comments"
        return JrawUtils.getSubredditPath(subreddit, "/comments");
    }
}
