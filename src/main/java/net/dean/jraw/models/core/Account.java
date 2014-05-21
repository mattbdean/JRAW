package net.dean.jraw.models.core;

import net.dean.jraw.models.Created;
import net.dean.jraw.models.JsonInteraction;
import net.dean.jraw.models.ThingType;
import org.codehaus.jackson.JsonNode;

/**
 * This class represents a redditor's account. See
 * <a href="https://github.com/reddit/reddit/wiki/JSON#account-implements-created">here</a> for more
 *
 * @author Matthew Dean
 */
public class Account extends Thing implements Created {
	public Account(JsonNode data) {
		super(data);
	}

	/**
	 * The user's comment karma
	 */
	@JsonInteraction
	public Integer getCommentKarma() {
		return data("comment_karma", Integer.class);
	}

	/**
	 * User has unread mail? Null if not your account
	 */
	@JsonInteraction(nullable = true)
	public Boolean hasMail() {
		return data("has_mail", Boolean.class);
	}

	/**
	 * User has provided an email address and got it verified? Null if not logged in and your account
	 */
	@JsonInteraction(nullable = true)
	public Boolean hasModMail() {
		return data("has_mod_mail", Boolean.class);
	}

	/**
	 * User has provided an email address and got it verified?
	 */
	@JsonInteraction
	public Boolean getHasVerifiedEmail() {
		return data("has_verified_email", Boolean.class);
	}

	/**
	 * Whether the logged-in user has this user set as a friend
	 */
	@JsonInteraction
	public Boolean getIsFriend() {
		return data("is_friend", Boolean.class);
	}

	/**
	 * Reddit gold status
	 */
	@JsonInteraction
	public Boolean hasGold() {
		return data("is_gold", Boolean.class);
	}

	/**
	 * Whether this account moderates any subreddits
	 */
	@JsonInteraction
	public Boolean isMod() {
		return data("is_mod", Boolean.class);
	}

	/**
	 * User's link karma
	 */
	@JsonInteraction
	public Integer getLinkKarma() {
		return data("link_karma", Integer.class);
	}

	/**
	 * Current modhash. Null if not your account
	 */
	@JsonInteraction(nullable = true)
	public String getModHash() {
		return data("modhash", String.class);
	}

	/**
	 * Whether this account is set to be over 18
	 */
	@JsonInteraction(nullable = true)
	public Boolean isOver18() {
		return data("over_18", Boolean.class);
	}

	@Override
	public ThingType getType() {
		return ThingType.ACCOUNT;
	}
}
