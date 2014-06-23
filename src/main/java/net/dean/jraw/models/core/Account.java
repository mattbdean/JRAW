package net.dean.jraw.models.core;

import net.dean.jraw.models.Created;
import net.dean.jraw.models.JsonInteraction;
import net.dean.jraw.models.ThingType;
import org.codehaus.jackson.JsonNode;

/**
 * Represents a redditor's account. See
 * <a href="https://github.com/reddit/reddit/wiki/JSON#account-implements-created">here</a> for more
 *
 * @author Matthew Dean
 */
public class Account extends Thing implements Created {
	public Account(JsonNode data) {
		super(data);
	}

	/**
	 * Gets the user's comment karma
	 * @return the user's comment karma
	 */
	@JsonInteraction
	public Integer getCommentKarma() {
		return data("comment_karma", Integer.class);
	}

	/**
	 * Checks if the user has unread mail. Returns null if the currently logged in account is not this one
	 * @return User has unread mail? Null if not your account
	 */
	@JsonInteraction(nullable = true)
	public Boolean hasMail() {
		return data("has_mail", Boolean.class);
	}

	/**
	 * Checks if the user has mod mail
	 * @return User has unread mod mail?
	 */
	@JsonInteraction(nullable = true)
	public Boolean hasModMail() {
		return data("has_mod_mail", Boolean.class);
	}

	/**
	 * Checks if the user has a verified email
	 * @return User has provided an email address and got it verified?
	 */
	@JsonInteraction
	public Boolean getHasVerifiedEmail() {
		return data("has_verified_email", Boolean.class);
	}

	/**
	 * Checks whether or not the logged-in user has this user set as a friend
	 * @return Whether the logged-in user has this user set as a friend
	 */
	@JsonInteraction
	public Boolean isFriend() {
		return data("is_friend", Boolean.class);
	}

	/**
	 * Checks if the user has Reddit Gold
	 * @return Reddit gold status
	 */
	@JsonInteraction
	public Boolean hasGold() {
		return data("is_gold", Boolean.class);
	}

	/**
	 * Checks whether this account moderates any subreddits
	 * @return True if this account moderates any subreddits
	 */
	@JsonInteraction
	public Boolean isMod() {
		return data("is_mod", Boolean.class);
	}

	/**
	 * Gets the user's link karma
	 * @return The user's link karma
	 */
	@JsonInteraction
	public Integer getLinkKarma() {
		return data("link_karma", Integer.class);
	}

	/**
	 * Gets the current modhash
	 * @return Current modhash, or null if not your account
	 */
	@JsonInteraction(nullable = true)
	public String getModHash() {
		return data("modhash");
	}

	/**
	 * Whether this account is set to be over 18
	 * @return If this account is set to be over 18
	 */
	@JsonInteraction(nullable = true)
	public Boolean isOver18() {
		return data("over_18", Boolean.class);
	}

	@Override
	public ThingType getType() {
		return ThingType.ACCOUNT;
	}

	/**
	 * Returns the name of this account (i.e. "spladug")
	 * @return The name of this account
	 */
	@Override
	public String getName() {
		return super.getName();
	}
}
