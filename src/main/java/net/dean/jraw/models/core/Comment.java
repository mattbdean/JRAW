package net.dean.jraw.models.core;

import net.dean.jraw.JrawUtils;
import net.dean.jraw.models.*;
import org.codehaus.jackson.JsonNode;

import java.net.URL;
import java.util.Date;

/**
 * Represents a comment on a link
 *
 * @author Matthew Dean
 */
public class Comment extends Thing implements Created, Distinguishable, Votable {

	/**
	 * Instantiates a new Comment
	 *
	 * @param dataNode The node that will be used to look up JSON properties
	 */
	public Comment(JsonNode dataNode) {
		super(dataNode);
	}

	/**
	 * Who approved this comment, nor null if the logged in user is not a moderator
	 */
	@JsonInteraction(nullable = true)
	public String getApprovedBy() {
		return data("approved_by", String.class);
	}

	/**
	 * The account name of the poster
	 */
	@JsonInteraction
	public String getAuthor() {
		return data("author", String.class);
	}

	/**
	 * The flair of the author. Subreddit specific.
	 */
	@JsonInteraction
	public Flair getAuthorFlair() {
		return new Flair(data("author_flair_css_class", String.class),
				data("author_flair_text", String.class));
	}

	/**
	 * Who removed this comment, or null if you are not a mod
	 */
	@JsonInteraction(nullable = true)
	public String getBannedBy() {
		return data("banned_by", String.class);
	}

	/**
	 * The raw, unformatted text. Includes markdown and escaped &lt;, &gt;, and &amp;s
	 */
	@JsonInteraction
	public String getBody() {
		return data("body", String.class);
	}

	/**
	 * The formatted HTML will display on Reddit
	 */
	@JsonInteraction
	public String getBodyHtml() {
		return data("body", String.class);
	}

	/**
	 * The edit date in UTC, or null if it has not been edited
	 */
	@JsonInteraction
	public Date getEditedDate() {
		JsonNode edited = data.get("edited");
		if (edited.isBoolean()) {
			// value of false, but Date cannot be false
			return null;
		}

		return new Date(edited.getLongValue() * 1000);
	}

	/**
	 * If the comment has been edited before
	 */
	@JsonInteraction
	public Boolean hasBeenEdited() {
		return getEditedDate() != null;
	}

	/**
	 * The number of times this comment has received Reddit Gold
	 */
	@JsonInteraction
	public Integer getTimesGilded() {
		return data("gilded", Integer.class);
	}

	/**
	 * The author of the parent link, or null if this comment is not being displayed outside of its own thread
	 */
	@JsonInteraction(nullable = true)
	public String getLinkAuthor() {
		return data("link_author", String.class);
	}

	/**
	 * The comments that have replied to this one
	 */
	@JsonInteraction
	public Listing<Comment> getReplies() {
		return new Listing<>(data.get("replies"), Comment.class);
	}

	/**
	 * The ID of the link this comment is in
	 */
	@JsonInteraction
	public String getLinkId() {
		return data("link_id", String.class);
	}

	/**
	 * The title of the parent link, or null if this comment is not being displayed outside of its own thread
	 */
	@JsonInteraction(nullable = true)
	public String getLinkTitle() {
		return data("link_title", String.class);
	}

	/**
	 * The author of the parent link, or null if this comment is not being displayed outside of its own thread
	 */
	@JsonInteraction(nullable = true)
	public URL getUrl() {
		String url = data("link_url", String.class);
		if (url != null) {
			return JrawUtils.newUrl(url);
		}

		return null;
	}

	/**
	 * The amount of times this comment has been reported, nor null if not a mod
	 */
	@JsonInteraction(nullable = true)
	public Integer getReportCount() {
		return data("num_reports", Integer.class);
	}

	/**
	 * The ID of the comment or link this comment is replying to
	 */
	@JsonInteraction
	public String getParentId() {
		return data("parent_id", String.class);
	}

	/**
	 * True if this post is saved by the logged in user, otherwise false
	 */
	@JsonInteraction
	public Boolean isSaved() {
		return data("saved", Boolean.class);
	}

	/**
	 * Whether the comment's score is current hidden
	 */
	@JsonInteraction
	public Boolean isScoreHidden() {
		return data("score_hidden", Boolean.class);
	}

	/**
	 * The subreddit the comment was posted in, excluding the "/r/" prefix (ex: "pics")
	 */
	@JsonInteraction
	public String getSubredditName() {
		return data("subreddit", String.class);
	}

	/**
	 * The ID of the subreddit in which this comment was posting
	 */
	@JsonInteraction
	public String getSubredditId() {
		return data("subreddit_id", String.class);
	}

	@Override
	public ThingType getType() {
		return ThingType.COMMENT;
	}

	/**
	 * The amount of upvotes this comment has received
	 */
	@JsonInteraction
	public Integer getUpvotes() {
		return getUpvotes(data);
	}

	/**
	 * The amount of upvotes this comment has received
	 */
	@JsonInteraction
	public Integer getDownvotes() {
		return getDownvotes(data);
	}
}
