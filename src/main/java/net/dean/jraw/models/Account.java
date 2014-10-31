package net.dean.jraw.models;

import net.dean.jraw.models.attr.Created;
import org.codehaus.jackson.JsonNode;

/**
 * Represents a redditor's account. See
 * <a href="https://github.com/reddit/reddit/wiki/JSON#account-implements-created">here</a> for more
 *
 * @author Matthew Dean
 */
public class Account extends Thing implements Created {
    /**
     * Instantiates a new Account
     * @param data The node to get data from
     */
    public Account(JsonNode data) {
        super(data);
    }

    /**
     * Gets the user's comment karma
     * @return the user's comment karma
     */
    @JsonProperty
    public Integer getCommentKarma() {
        return data("comment_karma", Integer.class);
    }

    /**
     * Checks whether or not the logged-in user has this user set as a friend
     * @return Whether the logged-in user has this user set as a friend
     */
    @JsonProperty
    public Boolean isFriend() {
        return data("is_friend", Boolean.class);
    }

    /**
     * Checks if the user has Reddit Gold
     * @return Reddit gold status
     */
    @JsonProperty
    public Boolean hasGold() {
        return data("is_gold", Boolean.class);
    }

    /**
     * Checks whether this account moderates any subreddits
     * @return True if this account moderates any subreddits
     */
    @JsonProperty
    public Boolean isMod() {
        return data("is_mod", Boolean.class);
    }

    /**
     * Gets the user's link karma
     * @return The user's link karma
     */
    @JsonProperty
    public Integer getLinkKarma() {
        return data("link_karma", Integer.class);
    }

    /**
     * Whether this account is set to be over 18
     * @return If this account is set to be over 18
     */
    @JsonProperty(nullable = true)
    public Boolean isOver18() {
        return data("over_18", Boolean.class);
    }

    @Override
    public ThingType getType() {
        return ThingType.ACCOUNT;
    }

    @Override
    public String getFullName() {
        return super.getFullName();
    }
}
