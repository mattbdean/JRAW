package net.dean.jraw.models;

import java.util.Date;

/**
 * This class represents a redditor's account.
 *
 * @author Matthew Dean
 */
public class Account extends Thing {
	/** The user's comment karma */
	@JsonAttribute(jsonName = "comment_karma")
	private Integer commentKarma;

	/** Registration date in epoch-seconds, local */
	@JsonAttribute(jsonName = "created")
	private Long created;

	/** Registration date in epoch-seconds, UTC */
	@JsonAttribute(jsonName = "created_utc")
	private Long createdUtc;

	/** User has unread mail? Null if not your account */
	@JsonAttribute(jsonName = "has_mail")
	private Boolean hasMail;

	/** User has unread mod mail? Null if not your account */
	@JsonAttribute(jsonName = "has_mod_mail")
	private Boolean hasModMail;

	/** User has provided an email address and got it verified? */
	@JsonAttribute(jsonName = "has_verified_email")
	private Boolean hasVerifiedEmail;

	/** Whether the logged-in user has this user set as a friend */
	@JsonAttribute(jsonName = "is_friend")
	private Boolean isFriend;

	/** Reddit gold status */
	@JsonAttribute(jsonName = "is_gold")
	private Boolean isGold;

	/** Whether this account moderates any subreddits */
	@JsonAttribute(jsonName = "is_mod")
	private Boolean isMod;

	/** User's link karma */
	@JsonAttribute(jsonName = "link_karma")
	private Integer linkKarma;

	/** Current modhash. Null present if not your account */
	@JsonAttribute(jsonName = "modhash")
	private String modHash;

	/** Whether this account is set to be over 18 */
	@JsonAttribute(jsonName = "over_18")
	private Boolean over18;

	/** The user's comment karma */
	public Integer getCommentKarma() {
		return commentKarma;
	}

	/** Registration date in local time */
	public Date getCreated() {
		// created in seconds, Date constructor wants milliseconds
		return new Date(created * 1000);
	}

	/** Registration date in UTC */
	public Date getCreatedUtc() {
		// created in seconds, Date constructor wants milliseconds
		return new Date(createdUtc * 1000);
	}

	/** User has unread mail? Null if not your account */
	public Boolean hasMail() {
		return hasMail;
	}

	/** User has provided an email address and got it verified? */
	public Boolean hasModMail() {
		return hasModMail;
	}

	/** User has provided an email address and got it verified? */
	public Boolean getHasVerifiedEmail() {
		return hasVerifiedEmail;
	}

	/** Whether the logged-in user has this user set as a friend */
	public Boolean getIsFriend() {
		return isFriend;
	}

	/** Reddit gold status */
	public Boolean hasGold() {
		return isGold;
	}

	/** Whether this account moderates any subreddits */
	public Boolean isMod() {
		return isMod;
	}

	/** User's link karma */
	public Integer getLinkKarma() {
		return linkKarma;
	}

	/** Current modhash. Null present if not your account */
	public String getModHash() {
		return modHash;
	}

	/** Whether this account is set to be over 18 */
	public Boolean getOver18() {
		return over18;
	}

	@Override
	public ThingType getType() {
		return ThingType.ACCOUNT;
	}
}
