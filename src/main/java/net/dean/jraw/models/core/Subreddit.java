package net.dean.jraw.models.core;

import net.dean.jraw.models.JsonInteraction;
import net.dean.jraw.models.RenderStringPair;
import net.dean.jraw.models.ThingType;
import org.codehaus.jackson.JsonNode;

import java.awt.*;
import java.net.URI;
import java.net.URL;

public class Subreddit extends Thing {

	/**
	 * Instantiates a new Subreddit
	 *
	 * @param dataNode The node to parse data from
	 */
	public Subreddit(JsonNode dataNode) {
		super(dataNode);
	}

	@JsonInteraction
	public Integer getAccountsActive() {
		return data("accounts_active", Integer.class);
	}

	@JsonInteraction
	public Integer getCommentScoreHideDuration() {
		return data("comment_score_hide_mins", Integer.class);
	}

	@JsonInteraction
	public RenderStringPair getSidebar() {
		return new RenderStringPair(data("description"), data("description_html"));
	}

	@JsonInteraction
	public String getDisplayName() {
		return data("display_name");
	}

	@JsonInteraction(nullable = true)
	public URL getHeaderImage() {
		return data("header_img", URL.class);
	}

	@JsonInteraction(nullable = true)
	public Dimension getHeaderSize() {
		JsonNode node = data.get("header_size");
		if (node.isNull()) {
			return null;
		}
		return new Dimension(node.get(0).asInt(-1), node.get(1).asInt(-1));
	}

	@JsonInteraction(nullable = true)
	public String getHeaderTitle() {
		return data("header_title");
	}

	@JsonInteraction
	public Boolean isOver18() {
		return data("over18", Boolean.class);
	}

	@JsonInteraction
	public String getPublicDescription() {
		return data("public_description");
	}

	@JsonInteraction
	public Boolean isPublic() {
		return data("public_traffic", Boolean.class);
	}

	@JsonInteraction
	public Long getSubscriberCount() {
		return data("subscribers", Long.class);
	}

	@JsonInteraction
	public Boolean isAllowingSelfPosts() {
		String type = data("submission_type");
		return type.equals("self") || type.equals("all");
	}

	@JsonInteraction
	public Boolean isAllowingLinks() {
		String type = data("submission_type");
		return type.equals("link") || type.equals("all");
	}

	@JsonInteraction
	public String getSubmitLinkLabel() {
		return data("submit_link_label");
	}

	@JsonInteraction
	public String getSubmitTextLabel() {
		return data("submit_text_label");
	}

	@JsonInteraction
	public Type getSubredditType() {
		return Type.valueOf(data("subreddit_type").toUpperCase());
	}

	@JsonInteraction
	public String getTitle() {
		return data("title");
	}

	@JsonInteraction
	public URI getRelativeLocation() {
		return data("url", URI.class);
	}

	@JsonInteraction
	public Boolean isUserBanned() {
		return data("user_is_banned", Boolean.class);
	}

	@JsonInteraction
	public Boolean isUserContributor() {
		return data("user_is_contributor", Boolean.class);
	}

	@JsonInteraction
	public Boolean isUserModerator() {
		return data("user_is_moderator", Boolean.class);
	}

	@JsonInteraction
	public Boolean isUserSubscriber() {
		return data("user_is_subscriber", Boolean.class);
	}



	public enum Type {
		PUBLIC, PRIVATE, RESTRICTED, GOLD_RESTRICTED, ARCHIVED;
	}



	@Override
	public ThingType getType() {
		return ThingType.SUBREDDIT;
	}
}
