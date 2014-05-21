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
		JsonNode node = data("approved_by");
		if (node != null) {
			return node.getTextValue();
		}

		return null;
	}

	/**
	 * The account name of the poster
	 */
	@JsonInteraction
	public String getAuthor() {
		return data("author").getTextValue();
	}

	/**
	 * The flair of the author. Subreddit specific.
	 */
	@JsonInteraction
	public Flair getAuthorFlair() {
		return new Flair(data("author_flair_css_class").getTextValue(),
				data("author_flair_text").getTextValue());
	}

	/**
	 * Who removed this comment, or null if you are not a mod
	 */
	@JsonInteraction(nullable = true)
	public String getBannedBy() {
		JsonNode node = data("banned_by");
		if (node != null) {
			return node.getTextValue();
		}

		return null;
	}

	/**
	 * The raw, unformatted text. Includes markdown and escaped &lt;, &gt;, and &amp;s
	 */
	@JsonInteraction
	public String getBody() {
		return data("body").getTextValue();
	}

	/**
	 * The formatted HTML will display on Reddit
	 */
	@JsonInteraction
	public String getBodyHtml() {
		return data("body").getTextValue();
	}

	/**
	 * The edit date in UTC, or null if it has not been edited
	 */
	@JsonInteraction
	public Date getEditedDate() {
		JsonNode edited = data("edited");
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
		return data("gilded").getIntValue();
	}

	/**
	 * The author of the parent link, or null if this comment is not being displayed outside of its own thread
	 */
	@JsonInteraction(nullable = true)
	public String getLinkAuthor() {
		JsonNode node = data("link_author");
		if (node != null) {
			return node.getTextValue();
		}

		return null;
	}

	/**
	 * The comments that have replied to this one
	 */
	@JsonInteraction
	public Listing<Comment> getReplies() {
		return new Listing<>(data("replies"), Comment.class);
	}

	/**
	 * The ID of the link this comment is in
	 */
	@JsonInteraction
	public String getLinkId() {
		return data("link_id").getTextValue();
	}

	/**
	 * The title of the parent link, or null if this comment is not being displayed outside of its own thread
	 */
	@JsonInteraction(nullable = true)
	public String getLinkTitle() {
		JsonNode node = data("link_title");
		if (node != null) {
			return node.getTextValue();
		}

		return null;
	}

	/**
	 * The author of the parent link, or null if this comment is not being displayed outside of its own thread
	 */
	@JsonInteraction(nullable = true)
	public URL getUrl() {
		JsonNode node = data("link_url");
		if (node != null) {
			return JrawUtils.newUrl(node.getTextValue());
		}

		return null;
	}

	/**
	 * The amount of times this comment has been reported, nor null if not a mod
	 */
	@JsonInteraction(nullable = true)
	public Integer getReportCount() {
		return data("num_reports").getIntValue();
	}

	/**
	 * The ID of the comment or link this comment is replying to
	 */
	@JsonInteraction
	public String getParentId() {
		return data("parent_id").getTextValue();
	}

	/**
	 * True if this post is saved by the logged in user, otherwise false
	 */
	@JsonInteraction
	public Boolean isSaved() {
		return data("saved").getBooleanValue();
	}

	/**
	 * Whether the comment's score is current hidden
	 */
	@JsonInteraction
	public Boolean isScoreHidden() {
		return data("score_hidden").getBooleanValue();
	}

	/**
	 * The subreddit the comment was posted in, excluding the "/r/" prefix (ex: "pics")
	 */
	@JsonInteraction
	public String getSubredditName() {
		return data("subreddit").getTextValue();
	}

	/**
	 * The ID of the subreddit in which this comment was posting
	 */
	@JsonInteraction
	public String getSubredditId() {
		return data("subreddit_id").getTextValue();
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
