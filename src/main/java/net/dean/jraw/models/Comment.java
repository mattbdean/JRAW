package net.dean.jraw.models;

import org.codehaus.jackson.JsonNode;

import java.net.URL;
import java.util.Date;

/**
 * Represents a comment on a link
 *
 * @author Matthew Dean
 */
public class Comment extends PublicContribution {

    /**
     * Instantiates a new Comment
     *
     * @param dataNode The node that will be used to look up JSON properties
     */
    public Comment(JsonNode dataNode) {
        super(dataNode);
    }

    /**
     * Gets who approved this comment, nor null if the logged in user is not a moderator
     * @return Who approved this comment
     */
    @JsonInteraction(nullable = true)
    public String getApprovedBy() {
        return data("approved_by");
    }

    /**
     * Gets the name of the account that posted this comment
     * @return The name the account that posted this comment
     */
    @JsonInteraction
    public String getAuthor() {
        return data("author");
    }

    /**
     * Gets the author's flair. Subreddit specific.
     * @return The subreddit-specific flair of the author
     */
    @JsonInteraction
    public Flair getAuthorFlair() {
        return new Flair(data("author_flair_css_class"),
                data("author_flair_text"));
    }

    /**
     * If the comment is controversial (has a large number of both upvotes and downvotes)
     * @return If the comment is controversial
     */
    @JsonInteraction
    public Boolean isControversial() {
        return data("controversiality", Integer.class) == 1;
    }

    /**
     * Who removed this comment, or null if you are not a mod
     * @return Who removed this comment
     */
    @JsonInteraction(nullable = true)
    public String getBannedBy() {
        return data("banned_by");
    }

    /**
     * Gets the body of the comment
     * @return The body of the comment
     */
    @JsonInteraction
    public RenderStringPair getBody() {
        return data("body", RenderStringPair.class);
    }

    /**
     * The edit date in UTC, or null if it has not been edited. Note that the Reddit API will return a boolean value
     * for some old edited comments, in which this method will return null.
     *
     * @return The edit date in UTC, or null if it has not been edited
     */
    @JsonInteraction
    public Date getEditedDate() {
        JsonNode edited = data.get("edited");
        if (edited.isBoolean()) {
            // API returns true for some old comments
            return null;
        }

        return new Date(edited.getLongValue() * 1000);
    }

    /**
     * Checks if the comment has been edited.
     *
     * @return If this comment has been edited before
     */
    @JsonInteraction
    public Boolean hasBeenEdited() {
        if (!data.has("edited")) {
            return false;
        }

        JsonNode edited = data.get("edited");
        if (edited.isBoolean()) {
            // If false, then the comment hasn't been edited.
            // On very old comments, the API will return true if it has been edited
            return edited.getBooleanValue();
        } else if (edited.isLong()) {
            // The comment has been edited, value is the time (in seconds) from the UTC epoch
            return true;
        }

        // Some other data type
        return false;
    }

    /**
     * Gets the author of the parent link
     * @return The author of the parent link, or null if this comment is not being displayed outside of its own thread
     */
    @JsonInteraction(nullable = true)
    public String getSubmissionAuthor() {
        return data("link_author");
    }

    /**
     * Gets the comments made in reply to this one
     * @return The comments made in reply to this one
     */
    @JsonInteraction(nullable = true)
    public Listing<Comment> getReplies() {
        // If it has no replies, the value for the replies key will be an empty string or null
        JsonNode replies = data.get("replies");
        if (replies.isNull() || (replies.isTextual() && replies.asText().isEmpty())) {
            return null;
        }
        return new Listing<>(data.get("replies").get("data"), Comment.class);
    }

    /**
     * Gets the ID of the submission this comment is located in
     * @return The ID of the submission this comment is located in
     */
    @JsonInteraction
    public String getSubmissionId() {
        return data("link_id");
    }

    /**
     * The title of the parent link, or null if this comment is not being displayed outside of its own thread
     * @return The title of the parent link
     */
    @JsonInteraction(nullable = true)
    public String getSubmissionTitle() {
        return data("link_title");
    }

    /**
     * The author of the parent submission
     * @return The author of the parent submission, or null if this comment is not being displayed outside of its own
     * thread
     */
    @JsonInteraction(nullable = true)
    public URL getUrl() {
        return data("link_url", URL.class);
    }

    /**
     * The amount of times this comment has been reported
     * @return The amount of times this comment has been reported, or null if not a mod
     */
    @JsonInteraction(nullable = true)
    public Integer getReportCount() {
        return data("num_reports", Integer.class);
    }

    /**
     * The ID of the comment or submission this comment is replying to
     * @return The ID of the comment or submission this comment is replying to
     */
    @JsonInteraction
    public String getParentId() {
        return data("parent_id");
    }

    /**
     * True if this post is saved by the logged in user, otherwise false
     * @return True if this post is saved by the logged in user, otherwise false
     */
    @JsonInteraction
    public Boolean isSaved() {
        return data("saved", Boolean.class);
    }

    /**
     * Whether the comment's score is currently hidden
     * @return True if the comment's score is hidden, false if not
     */
    @JsonInteraction
    public Boolean isScoreHidden() {
        return data("score_hidden", Boolean.class);
    }

    /**
     * The subreddit the comment was posted in, excluding the "/r/" prefix (ex: "pics")
     * @return The name of the subreddit the comment was posted in
     */
    @JsonInteraction
    public String getSubredditName() {
        return data("subreddit");
    }

    /**
     * The ID of the subreddit in which this comment was posted in
     * @return The ID of the subreddit in which this comment was posted in
     */
    @JsonInteraction
    public String getSubredditId() {
        return data("subreddit_id");
    }

    @Override
    public ThingType getType() {
        return ThingType.COMMENT;
    }
}
