package net.dean.jraw.models;

import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Date;

/**
 * Represents a comment on a Submission.
 *
 * @author Matthew Dean
 */
@Model(kind = Model.Kind.COMMENT)
public class Comment extends PublicContribution {

    /**
     * Instantiates a new Comment
     */
    public Comment(JsonNode dataNode) {
        super(dataNode);
    }

    /** Gets who approved this comment, or null if the logged in account is not a moderator */
    @JsonProperty(nullable = true)
    public String getApprovedBy() {
        return data("approved_by");
    }

    /** Gets the name of the account that posted this comment */
    @JsonProperty
    public String getAuthor() {
        return data("author");
    }

    /** Gets the author's subreddit-specific flair. */
    @JsonProperty(nullable = true)
    public Flair getAuthorFlair() {
        if (data.get("author_flair_css_class").isNull() && data.get("author_flair_text").isNull())
            return null;
        return new Flair(data("author_flair_css_class"), data("author_flair_text"));
    }

    /** Checks if the comment is controversial (has a large number of both upvotes and downvotes) */
    @JsonProperty
    public Boolean isControversial() {
        return data.has("controversiality") && data("controversiality", Integer.class) == 1;
    }

    /** Gets who removed this comment, or null if you are not a mod */
    @JsonProperty(nullable = true)
    public String getBannedBy() {
        return data("banned_by");
    }

    /** Gets the body of the comment */
    @JsonProperty
    public String getBody() {
        return data("body");
    }

    /**
     * Gets the edit date in UTC, or null if it has not been edited. Note that the Reddit API will return a boolean value
     * for some old edited comments, in which this method will return null. If this comment was retrieved via the inbox,
     * this will also return null.
     *
     * @return The edit date in UTC, or null if it has not been edited
     * @see #hasBeenEdited()
     */
    @JsonProperty(nullable = true)
    public Date getEditDate() {
        if (!data.has("edited")) {
            return null;
        }

        JsonNode edited = data.get("edited");
        if (edited.isBoolean()) {
            // API returns true for some old comments
            return null;
        }

        return new Date(edited.longValue() * 1000);
    }

    /**
     * Checks if the comment has been edited.
     * @see #getEditDate()
     */
    @JsonProperty
    public Boolean hasBeenEdited() {
        if (!data.has("edited")) {
            return false;
        }

        JsonNode edited = data.get("edited");
        if (edited.isBoolean()) {
            // If false, then the comment hasn't been edited.
            // On very old comments, the API will return true if it has been edited
            return edited.booleanValue();
        } else if (edited.isLong()) {
            // The comment has been edited, value is the time (in seconds) from the UTC epoch
            return true;
        }

        // Some other data type
        return false;
    }

    /** Gets the fullname of the user who posted the submission. */
    @JsonProperty(nullable = true)
    public String getSubmissionAuthor() {
        return data("link_author");
    }

    /** Gets the ID of the submission this comment is located in */
    @JsonProperty
    public String getSubmissionId() {
        return data("link_id");
    }

    /** Gets the title of the parent link, or null if this comment is not being displayed outside of its own thread */
    @JsonProperty(nullable = true)
    public String getSubmissionTitle() {
        return data("link_title");
    }

    /**
     * Gets the author of the parent submission, or null if this comment is not being displayed outside of its own
     * thread
     */
    @JsonProperty(nullable = true)
    public String getUrl() {
        return data("link_url");
    }

    /** Gets the amount of times this comment has been reported, or null if the logged in user is not a mod. */
    @JsonProperty(nullable = true)
    public Integer getReportCount() {
        return data("num_reports", Integer.class);
    }

    /**
     * Gets the ID of the comment or submission this comment is replying to. If this is a top-level comment, then the
     * submission ID will be returned.
     */
    @JsonProperty
    public String getParentId() {
        return data("parent_id");
    }

    /** Checks if this post has been saved by the logged in user */
    @JsonProperty
    public Boolean isSaved() {
        return data("saved", Boolean.class);
    }

    /** Checks if the comment's score is currently hidden */
    @JsonProperty
    public Boolean isScoreHidden() {
        return data("score_hidden", Boolean.class);
    }

    /** The subreddit the comment was posted in, excluding the "/r/" prefix (ex: "pics") */
    @JsonProperty
    public String getSubredditName() {
        return data("subreddit");
    }

    /** The ID of the subreddit in which this comment was posted in */
    @JsonProperty
    public String getSubredditId() {
        return data("subreddit_id");
    }
}
