package net.dean.jraw.paginators;

import net.dean.jraw.util.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Comment;

/**
 * A Paginator geared towards viewing the comments on a subreddit. See
 * <a href="https://www.reddit.com/pics/comments">here</a> for example data.
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
