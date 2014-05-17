package net.dean.jraw.models.core;

import net.dean.jraw.JrawUtils;
import net.dean.jraw.models.*;
import org.codehaus.jackson.JsonNode;

import java.net.URI;
import java.net.URL;
import java.util.Date;

public class Link extends Thing implements Votable, Created, Distinguishable {
	private Listing<Comment> comments;

	public Link(JsonNode dataNode, Listing<Comment> comments) {
		super(dataNode);
		this.comments = comments;
	}

	@Override
	public ThingType getType() {
		return ThingType.LINK;
	}

	/** The name of the poster, or null if this is a promotional link */
	@JsonInteraction
	public String getAuthor() {
		return data("author").getTextValue();
	}

	/** The flair used for the poster of the link (subreddit specific) */
	@JsonInteraction
	public Flair getAuthorFlair() {
		return new Flair(data("author_flair_css_class").getTextValue(),
				data("author_flair_text").getTextValue());
	}

	/** Whether the user has clicked this link. Most likely false unless the user has Reddit Gold */
	@JsonInteraction
	public Boolean isClicked() {
		return data("clicked").getBooleanValue();
	}

	/** The domain of this link. Self posts will be "self.reddit.com". Other examples: "en.wikipedia.org" and "s3.amazon.com" */
	@JsonInteraction
	public String getDomain() {
		return data("domain").getTextValue();
	}

	/** True if this link is a self post */
	@JsonInteraction
	public Boolean isSelfPost() {
		return data("is_self").getBooleanValue();
	}

	/** True if the post is hidden by the logged in user, false if not logged in or not hidden */
	@JsonInteraction
	public Boolean isHidden() {
		return data("hidden").getBooleanValue();
	}

	/**
	 * The net score of the link (upvotes minus downvotes). Note: the score is
	 * <a href="http://www.reddit.com/r/woahdude/comments/1vehg6/gopro_on_the_back_of_an_eagle/cersffj">fuzzed</a>
	 */
	@JsonInteraction
	public Integer getScore() {
		return data("score").getIntValue();
	}

	/** Gets the flair used in this link */
	@JsonInteraction
	public Flair getLinkFlair() {
		return new Flair(data("link_flair_css_class").getTextValue(),
				data("link_flair_text").getTextValue());
	}


	// TODO "media" and "embed_media" objects
//	/** Used for streaming video. Detailed information about the video and its origins. */
//  @JsonInteraction
//	public Object media;
//
//	/** Used for streaming video. Technical embed specific information is found here. */
//  @JsonInteraction
//	public Object media_embed;

	/** The number of comments that belong to this link. Includes removed comments. */
	@JsonInteraction
	public Integer getCommentCount() {
		return data("num_comments").getIntValue();
	}

	/** Whether or not the post is tagged as NSFW */
	@JsonInteraction
	public Boolean isOver18() {
		return data("over_18").getBooleanValue();
	}

	/** Relative URL (of reddit.com) of the permanent URL for this Link */
	@JsonInteraction
	public URI getPermalink() {
		return JrawUtils.newUri(data("permalink").getTextValue());
	}

	/** True if saved by the logged in user */
	@JsonInteraction
	public Boolean isSaved() {
		return data("saved").getBooleanValue();
	}

	/**
	 * The raw text of the self post. The string is unformatted, so it includes Markdown markup such as "**" for bold.
	 * HTML entities such as '&lt;', '&gt;', and '&amp;' are escaped.
	 */
	@JsonInteraction
	public String getSelftext() {
		return data("selftext").getTextValue();
	}

	/** The formatted, HTML version of ${@link #getSelftext()} */
	@JsonInteraction
	public String getSelftextHtml() {
		return data("selftext_html").getTextValue();
	}

	/** The subreddit that the link is posted in (ex: "pics", "funny") */
	@JsonInteraction
	public String getSubredditName() {
		return data("subreddit").getTextValue();
	}

	/** The full name of the subreddit which the link is posted in (ex: "t5_2s5oq" */
	@JsonInteraction
	public String getSubredditId() {
		return data("subreddit_id").getTextValue();
	}

	/** The full URL to the thumbnail for this link */
	@JsonInteraction(nullable = true)
	public URL getThumbnail() {
		String thumb = data("thumbnail").getTextValue();
		if (thumb.equals("self")) {
			return null;
		}

		return JrawUtils.newUrl(thumb);
	}

	/** The title of the link. May contain newlines (\n). */
	@JsonInteraction
	public String getTitle() {
		return data("title").getTextValue();
	}

	/** The URL of this post, or the permalink if this is a self post */
	@JsonInteraction
	public URL getUrl() {
		return JrawUtils.newUrl(data("url").getTextValue());
	}

	/** Indicates if the link has been edited. Null if it has not. */
	@JsonInteraction
	public Date getEdited() {
		JsonNode node = data("edited");

		// "edited" is false if it hasn't been edited, so return null instead
		if (node.isBoolean() && !node.getBooleanValue()) {
			return null;
		}

		return new Date(data("edited").getLongValue() * 1000);
	}

	/** Gets the comments of this Link */
	public Listing<Comment> getComments() {
		return comments;
	}

	@JsonInteraction
	public DistinguishedState getDistinguishedState() {
		return getDistinguishedState(data);
	}

	/** True if the post is set as the sticky in its respective subreddit */
	@JsonInteraction
	public Boolean isStickied() {
		return data("stickied").getBooleanValue();
	}

	@JsonInteraction
	public VoteType getVote() {
		return getVote(data);
	}

	@JsonInteraction
	public Date getCreated() {
		return getCreated(data);
	}

	@JsonInteraction
	public Date getCreatedUtc() {
		return getCreatedUtc(data);
	}

	@JsonInteraction
	public Integer getUpvotes() {
		return getUpvotes(data);
	}

	@JsonInteraction
	public Integer getDownvotes() {
		return getDownvotes(data);
	}
}
