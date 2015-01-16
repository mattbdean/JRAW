package net.dean.jraw.models;

import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import net.dean.jraw.models.meta.SubmissionSerializer;
import org.codehaus.jackson.JsonNode;

import java.util.Date;

/**
 * Represents content that the user has submitted, whether that be a self post or a link. More information can be found
 * <a href="https://github.com/reddit/reddit/wiki/JSON#link-implements-votable--created">here</a>.
 */
@Model(kind = Model.Kind.LINK, serializer = SubmissionSerializer.class, validate = false)
public class Submission extends PublicContribution {
    /**
     * The comments that belong to this link
     */
    private Listing<Comment> comments;

    /**
     * Instantiates a new Submission with no comments
     *
     * @param dataNode The JsonNode that is used to look up JSON values
     */
    public Submission(JsonNode dataNode) {
        this(dataNode, null);
    }

    /**
     * Instantiates a new Submission
     *
     * @param dataNode The JsonNode that is used to look up JSON values
     * @param comments The comments that belong to this link
     */
    public Submission(JsonNode dataNode, Listing<Comment> comments) {
        super(dataNode);
        this.comments = comments;
    }

    /**
     * The name of the poster, or null if this is a promotional link
     * @return The name of the poster
     */
    @JsonProperty
    public String getAuthor() {
        return data("author");
    }

    /**
     * The flair used for the poster of the link (subreddit specific)
     * @return Poster's flair
     */
    @JsonProperty
    public Flair getAuthorFlair() {
        return new Flair(data("author_flair_css_class"),
                data("author_flair_text"));
    }

    /**
     * Whether the user has clicked this link. Most likely false unless the user has Reddit Gold
     * @return If the user has clicked this link
     */
    @JsonProperty
    public Boolean isClicked() {
        return data("clicked", Boolean.class);
    }

    /**
     * The domain of this link. Self posts will be "self.reddit.com". Other examples: "en.wikipedia.org" and "s3.amazon.com"
     * @return This link's domain
     */
    @JsonProperty
    public String getDomain() {
        return data("domain");
    }

    /**
     * The type of submission
     * @return The type of submission
     */
    @JsonProperty
    public Boolean isSelfPost() {
        return data.get("is_self").getBooleanValue();
    }

    /**
     * True if the post is hidden by the logged in user, false if not logged in or not hidden
     * @return True if the post is hidden by the logged in user, false if not logged in or not hidden
     */
    @JsonProperty
    public Boolean isHidden() {
        return data("hidden", Boolean.class);
    }

    /**
     * The ratio of upvotes to downvotes
     * @return The ratio of upvotes to downvotes
     */
    @JsonProperty
    public Double getUpvoteRatio() {
        return data("upvote_ratio", Double.class);
    }

    /**
     * This link's flair
     * @return This link's flair
     */
    @JsonProperty
    public Flair getSubmissionFlair() {
        return new Flair(data("link_flair_css_class"),
                data("link_flair_text"));
    }


    /**
     * Gets a simplified version of the oEmbed data that includes embedded HTML
     * @return A simplified version of the oEmbed data
     */
    @JsonProperty(nullable = true)
    public EmbeddedMedia getEmbeddedMedia() {
        return new EmbeddedMedia(data.get("media_embed"));
    }

    /**
     * Gets the oEmbed data of this submission
     * @return The oEmbed data of this submission
     */
    @JsonProperty(nullable = true)
    public OEmbed getOEmbedMedia() {
        if (!data.has("media")) return null;
        if (data.get("media").size() == 0) return null;

        return new OEmbed(data.get("media").get("oembed"));
    }

    /**
     * The number of comments that belong to this submission. Includes removed comments.
     * @return The total number of comments that belong to this submission
     */
    @JsonProperty
    public Integer getCommentCount() {
        return data("num_comments", Integer.class);
    }

    /**
     * Whether or not the post is tagged as NSFW (not safe for work)
     * @return If the post is tagged as NSFW
     */
    @JsonProperty
    public Boolean isNsfw() {
        return data("over_18", Boolean.class);
    }

    /**
     * Relative URL (of reddit.com) of the permanent URL for this Submission
     * @return The permalink of this submission
     */
    @JsonProperty
    public String getPermalink() {
        return data("permalink");
    }

    /**
     * True if saved by the logged in user
     * @return True if saved by the logged in user
     */
    @JsonProperty
    public Boolean isSaved() {
        return data("saved", Boolean.class);
    }

    /**
     * The raw text of the self post. The string is unformatted, so it includes Markdown markup such as "**" for bold.
     * HTML entities such as '&amp;lt;', '&amp;gt;', and '&amp;amp;' are escaped.
     * @return The raw text of the self post
     */
    @JsonProperty
    public String getSelftext() {
        return data("selftext");
    }

    /**
     * The subreddit that the submission is posted in (ex: "pics", "funny")
     * @return The subreddit that the submission was posted in
     */
    @JsonProperty
    public String getSubredditName() {
        return data("subreddit");
    }

    /**
     * The full name of the subreddit which the link is posted in (ex: "t5_2s5oq")
     * @return The full name of the subreddit
     */
    @JsonProperty
    public String getSubredditId() {
        return data("subreddit_id");
    }

    /**
     * The full URL to the thumbnail for this submission
     * @return The URL to this submission's thumbnail
     */
    @JsonProperty(nullable = true)
    public String getThumbnail() {
        String thumb = data.get("thumbnail").getTextValue();
        if (getThumbnailType() != ThumbnailType.URL) {
            return null;
        }

        return thumb;
    }

    /**
     * Gets this Submission's thumbnail type. Different thumbnail values are returned for different reasons, such as if
     * the post is NSFW, a self post, etc. If the type is {@link ThumbnailType#URL}, then Reddit has created a thumbnail
     * for this post.
     * @return This Submission's thumbnail type
     */
    @JsonProperty
    public ThumbnailType getThumbnailType() {
        String thumb = data.get("thumbnail").getTextValue();

        // Try to find the type
        ThumbnailType type;

        if (thumb.isEmpty()) {
            type = ThumbnailType.NONE;
        } else {
            try {
                type = ThumbnailType.valueOf(thumb.toUpperCase());
            } catch (IllegalArgumentException e) {
                // "thumbnail"'s value is a URL
                type = ThumbnailType.URL;
            }
        }

        return type;
    }

    /**
     * The title of the submission. May contain newlines (\n).
     * @return The title of the submission
     */
    @JsonProperty
    public String getTitle() {
        return data("title");
    }

    /**
     * The URL of this post, or the permalink if this is a self post
     * @return This submission's URL
     */
    @JsonProperty
    public String getUrl() {
        return data("url");
    }

    /**
     * Indicates if the link has been edited. Null if it has not.
     * @return The UTC date when this submission was edited, null if it has not been edited
     */
    @JsonProperty
    public Date getEdited() {
        JsonNode node = data.get("edited");

        // "edited" is false if it hasn't been edited, so return null instead
        if (node.isBoolean() && !node.getBooleanValue()) {
            return null;
        }

        return new Date(node.getLongValue() * 1000);
    }

    /**
     * Gets the comments of this Submission
     * @return This Submission's comments
     */
    @JsonProperty(nullable = true)
    public Listing<Comment> getComments() {
        return comments;
    }

    /**
     * True if the post is set as the sticky in its respective subreddit
     * @return If this submission is a sticky
     */
    @JsonProperty
    public Boolean isStickied() {
        return data("stickied", Boolean.class);
    }

    /**
     * Gets a URL on the redd.it domain. For example, <a href="http://redd.it/92dd8">http://redd.it/92dd8</a>
     * @return The short URL to this post
     */
    public String getShortURL() {
        return "http://redd.it/" + getId();
    }

    @Override
    public Date getCreated() {
        return _getCreated();
    }

    @Override
    public Date getCreatedUtc() {
        return _getCreatedUtc();
    }

    @Override
    public DistinguishedStatus getDistinguishedStatus() {
        return _getDistinguishedStatus();
    }

    @Override
    public Integer getTimesGilded() {
        return _getTimesGilded();
    }

    @Override
    public Integer getUpvotes() {
        return _getUpvotes();
    }

    @Override
    public Integer getDownvotes() {
        return _getDownvotes();
    }

    @Override
    public Integer getScore() {
        return _getScore();
    }

    @Override
    public VoteDirection getVote() {
        return _getVote();
    }

    /**
     * Represents a list of possible return values for the "thumbnail" JsonNode. All of the values in this enum can be
     * returned by the Reddit API, except for {@link #URL} and {@link #NONE}. If {@code URL} is returned, then Reddit
     * has created a thumbnail for specifically for that post. If {@code NONE} is returned, then there is no thumbnail
     * available.
     */
    public static enum ThumbnailType {
        /** For when a post is marked as NSFW */
        NSFW,
        /** For when reddit couldn't create one */
        DEFAULT,
        /** For self posts */
        SELF,
        /** No thumbnail */
        NONE,
        /** A custom thumbnail that can be accessed by calling {@link Submission#getThumbnail()} */
        URL
    }
}
