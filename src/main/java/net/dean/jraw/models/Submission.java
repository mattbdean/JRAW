package net.dean.jraw.models;

import com.fasterxml.jackson.databind.JsonNode;
import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import net.dean.jraw.models.meta.SubmissionSerializer;
import net.dean.jraw.util.JrawUtils;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

/**
 * Represents content that the user has submitted, whether that be a self post or a link. More information can be found
 * <a href="https://github.com/reddit/reddit/wiki/JSON#link-implements-votable--created">here</a>.
 */
@Model(kind = Model.Kind.LINK, serializer = SubmissionSerializer.class, validate = false)
public final class Submission extends PublicContribution {
    private CommentNode rootNode;

    /** Instantiates a new Submission with no comments */
    public Submission(JsonNode dataNode) {
        this(dataNode, null);
        saved = data("saved", Boolean.class);
    }
    public boolean saved = false;

    /**
     * Instantiates a new Submission
     *
     * @param comments Gets the root CommentNode. See {@link CommentNode#CommentNode(String, List, MoreChildren, CommentSort)}.
     */
    public Submission(JsonNode dataNode, CommentNode comments) {
        super(dataNode);
        this.rootNode = comments;
        saved = data("saved", Boolean.class);

    }

    /** Gets who approved this submission, or null if the logged in account is not a moderator */
    @JsonProperty(nullable = true)
    public String getApprovedBy() {
        return data("approved_by");
    }

    /** Gets who removed this submission, or null if you are not a mod */
    @JsonProperty(nullable = true)
    public String getBannedBy() {
        return data("banned_by");
    }

    /** Gets the name of the poster, or null if this is a promotional link */
    @JsonProperty
    public String getAuthor() {
        return data("author");
    }

    /** Gets the flair used for the poster of the link (subreddit specific) */
    @JsonProperty(nullable = true)
    public Flair getAuthorFlair() {
        if (data.get("author_flair_css_class").isNull() && data.get("author_flair_text").isNull())
            return null;
        return new Flair(data("author_flair_css_class"),
                data("author_flair_text"));
    }


    /** Checks whether the user has clicked this link. Most likely false unless the user has Reddit Gold. */
    @JsonProperty
    public Boolean isClicked() {
        return data("clicked", Boolean.class);
    }

    /**
     * Gets the domain of this link. Self posts will be "self.{subreddit}".
     */
    @JsonProperty
    public String getDomain() {
        return data("domain");
    }

    /**
     * Gets the type of submission
     * @return Gets the type of submission
     */
    @JsonProperty
    public Boolean isSelfPost() {
        return data.get("is_self").booleanValue();
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
     * Gets the ratio of upvotes to downvotes
     * @return Gets the ratio of upvotes to downvotes
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
     * Gets the oEmbed data of this submission
     * @return Gets the oEmbed data of this submission
     */
    @JsonProperty(nullable = true)
    public OEmbed getOEmbedMedia() {
        if (!data.has("media")) return null;
        if (data.get("media").size() == 0) return null;

        return new OEmbed(data.get("media").get("oembed"));
    }

    /**
     * Gets the number of comments that belong to this submission. Includes removed comments.
     * @return Gets the total number of comments that belong to this submission
     */
    @JsonProperty
    public Integer getCommentCount() {
        return data("num_comments", Integer.class);
    }

    /**
     * Gets the localized number of comments that belong to this submission. Includes removed comments.
     * @return Gets the total number of comments that belong to this submission localized for the current locale
     */
    public String getLocalizedCommentCount() {
        try {
            return NumberFormat.getInstance().format(getCommentCount());
        } catch (final IllegalArgumentException ex) {
            return null;
        }
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
     * @return Gets the permalink of this submission
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
        return saved;
    }

    /**
     * Gets the raw text of the self post. The string is unformatted, so it includes Markdown markup such as "**" for bold.
     * HTML entities such as '&amp;lt;', '&amp;gt;', and '&amp;amp;' are escaped.
     * @return Gets the raw text of the self post
     */
    @JsonProperty
    public String getSelftext() {
        return data("selftext");
    }

    /**
     * Gets the subreddit that the submission is posted in (ex: "pics", "funny")
     * @return Gets the subreddit that the submission was posted in
     */
    @JsonProperty
    public String getSubredditName() {
        return data("subreddit");
    }

    /**
     * Gets the fullname of the subreddit which the link is posted in (ex: "t5_2s5oq")
     * @return Gets the fullname of the subreddit
     */
    @JsonProperty
    public String getSubredditId() {
        return data("subreddit_id");
    }

    /**
     * Gets the full URL to the thumbnail for this submission
     * @return Gets the URL to this submission's thumbnail
     */
    @JsonProperty(nullable = true)
    public String getThumbnail() {
        String thumb = data.get("thumbnail").textValue();
        if (getThumbnailType() != ThumbnailType.URL) {
            return null;
        }

        return thumb;
    }

    /**
     * Gets this Submission's thumbnail type. Different thumbnail values are returned for different reasons, such as if
     * the post is NSFW, a self post, etc. If the type is {@link ThumbnailType#URL}, then reddit has created a thumbnail
     * for this post.
     * @return This Submission's thumbnail type
     */
    @JsonProperty
    public ThumbnailType getThumbnailType() {
        JsonNode node = data.get("thumbnail");
        if (node.isNull()) {
            return ThumbnailType.NONE;
        }

        String thumb = node.textValue();

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
     * Gets the title of the submission. May contain newlines (\n).
     * @return Gets the title of the submission
     */
    @JsonProperty
    public String getTitle() {
        return data("title");
    }

    /**
     * Gets the URL of this post, or the permalink if this is a self post
     * @return This submission's URL
     */
    @JsonProperty
    public String getUrl() {
        return data("url");
    }

    /** Gets the date in UTC when this submission was edited, null if it has not been edited */
    @JsonProperty
    public Date getEdited() {
        JsonNode node = data.get("edited");

        // "edited" is false if it hasn't been edited, so return null instead
        if (node.isBoolean() && !node.booleanValue()) {
            return null;
        }

        return new Date(node.longValue() * 1000);
    }

    /** Gets this Submission's root CommentNode. See {@link CommentNode} for more information about this node. */
    @JsonProperty(nullable = true)
    public CommentNode getComments() {
        return rootNode;
    }

    /** Checks if the post is set as the sticky in its respective subreddit */
    @JsonProperty
    public Boolean isStickied() {
        return data("stickied", Boolean.class);
    }

    /**
     * Gets the type of content reddit thinks the post will show the user if it is clicked on. For example, a link to
     * Imgur would most likely have a post hint of {@link net.dean.jraw.models.Submission.PostHint#IMAGE IMAGE}.
     */
    @JsonProperty
    public PostHint getPostHint() {
        return PostHint.byJsonKey(data("post_hint"));
    }

    /**
     * Gets the suggested sorting for this thread's comments. For example, {@link CommentSort#QA} would likely be the
     * suggested sorting for an AMA. May be null.
     */
    @JsonProperty(nullable = true)
    public CommentSort getSuggestedSort() {
        String key = "suggested_sort";
        if (data.get(key).isNull()) {
            return null;
        }
        try {
            return CommentSort.valueOf(data(key).toUpperCase());
        } catch(IllegalArgumentException e){
            return CommentSort.CONFIDENCE;
        }
    }

    /**
     * Checks if this is a locked thread. If true, then no more commenting or voting is allowed, on both this submission
     * or its comments.
     */
    @JsonProperty
    public Boolean isLocked() {
        return data("locked", Boolean.class);
    }

    @JsonProperty
    public Boolean isQuarantined() {
        return data("quarantined", Boolean.class);
    }

    /**
     * Get the thumbnails for this submission, if any. May return null.
     */
    @JsonProperty(nullable = true)
    public Thumbnails getThumbnails() {
        String key = "preview";
        if (!data.has(key) || data.get(key).isNull()) {
            return null;
        }
        return new Thumbnails(data.get(key).get("images").get(0));
    }

    /** Gets a URL on the redd.it domain. For example, <a href="http://redd.it/92dd8">http://redd.it/92dd8</a> */
    public String getShortURL() {
        return "http://redd.it/" + JrawUtils.urlEncode(getId());
    }

    @Override
    public Date getCreated() {
        return _getCreated();
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
    public Integer getScore() {
        return _getScore();
    }

    public String getLocalizedScore() {
        try {
            return NumberFormat.getInstance().format(getScore());
        } catch (final IllegalArgumentException ex) {
            return null;
        }
    }

    boolean voted;
    public boolean voted(){
        return voted;
    }

    public void setVoted(Boolean b){
        voted= b;
    }


    boolean upvoted;
    public void setVote(Boolean b){
        upvoted = b;
    }
    public boolean getIsUpvoted(){
        return upvoted;
    }

    @Override
    public VoteDirection getVote() {
        return _getVote();
    }

    /**
     * Represents a list of possible return values for the "thumbnail" JsonNode. All of the values in this enum can be
     * returned by the reddit API, except for {@link #URL} and {@link #NONE}. If {@code URL} is returned, then Reddit
     * has created a thumbnail for specifically for that post. If {@code NONE} is returned, then there is no thumbnail
     * available.
     */
    public enum ThumbnailType {
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

    /**
     * This enum is a list of the hints reddit can give about the type of content that lies at a certain URL. For
     * example, a self post will have a PostHint of {@link #SELF}, while a submission with a link to Imgur might have a
     * PostHint of {@link #IMAGE}.
     */
    public enum PostHint {
        SELF("self"),
        LINK("link"),
        IMAGE("image"),
        VIDEO("rich:video"),
        UNKNOWN("");

        private String jsonKey;
        PostHint(String jsonKey) {
            this.jsonKey = jsonKey;
        }

        public static PostHint byJsonKey(String postHint) {
            for (PostHint p : values()) {
                if (p.jsonKey.equals(postHint)) {
                    return p;
                }
            }

            return UNKNOWN;
        }
    }
}
