package net.dean.jraw.models.core;

import net.dean.jraw.models.Created;
import net.dean.jraw.models.JsonInteraction;
import net.dean.jraw.models.ThingType;
import org.codehaus.jackson.JsonNode;

import java.util.Date;

/**
 * This class represents a redditor's account.
 *
 * @author Matthew Dean
 */
public class Account extends Thing implements Created {
	public Account(JsonNode data) {
		super(data);
	}

	/** Registration date in local time */
	@JsonInteraction
	public Date getCreated() {
		return getCreated(data);
	}

	/** Registration date in UTC */
	@JsonInteraction
	public Date getCreatedUtc() {
		return getCreatedUtc(data);
	}

	/** The user's comment karma */
	@JsonInteraction
	public Integer getCommentKarma() {
		return data("comment_karma").getIntValue();
	}

	/** User has unread mail? Null if not your account */
	@JsonInteraction(nullable = true)
	public Boolean hasMail() {
		JsonNode node = data("has_mail");
		if (node != null) {
			return node.getBooleanValue();
		}
		return null;
	}

	/** User has provided an email address and got it verified? Null if not logged in and your account */
	@JsonInteraction(nullable = true)
	public Boolean hasModMail() {
		JsonNode node = data("has_mod_mail");
		if (node != null) {
			return node.getBooleanValue();
		}
		return null;
	}

	/** User has provided an email address and got it verified? */
	@JsonInteraction
	public Boolean getHasVerifiedEmail() {
		return data("has_verified_email").getBooleanValue();
	}

	/** Whether the logged-in user has this user set as a friend */
	@JsonInteraction
	public Boolean getIsFriend() {
		return data("is_friend").getBooleanValue();
	}

	/** Reddit gold status */
	@JsonInteraction
	public Boolean hasGold() {
		return data("is_gold").getBooleanValue();
	}

	/** Whether this account moderates any subreddits */
	@JsonInteraction
	public Boolean isMod() {
		return data("is_mod").getBooleanValue();
	}

	/** User's link karma */
	@JsonInteraction
	public Integer getLinkKarma() {
		return data("link_karma").getIntValue();
	}

	/** Current modhash. Null if not your account */
	@JsonInteraction(nullable = true)
	public String getModHash() {
		JsonNode node = data("modhash");
		if (node != null) {
			return node.getTextValue();
		}
		return null;
	}

	/** Whether this account is set to be over 18 */
	@JsonInteraction(nullable = true)
	public Boolean isOver18() {
		JsonNode node = data("over_18");
		if (node != null) {
			return node.getBooleanValue();
		}
		return null;
	}

	@Override
	public ThingType getType() {
		return ThingType.ACCOUNT;
	}
}
