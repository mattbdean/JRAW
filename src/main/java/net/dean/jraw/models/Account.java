package net.dean.jraw.models;

import net.dean.jraw.models.attr.Created;
import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Date;

/**
 * Represents a redditor's account.
 *
 * @author Matthew Dean
 */
@Model(kind = Model.Kind.ACCOUNT)
public class Account extends Thing implements Created {
    /** Instantiates a new Account */
    public Account(JsonNode data) {
        super(data);
    }

    /** Gets the user's comment karma */
    @JsonProperty
    public Integer getCommentKarma() {
        return data("comment_karma", Integer.class);
    }

    /** Checks whether or not the logged-in user has this user set as a friend */
    @JsonProperty
    public Boolean isFriend() {
        return data("is_friend", Boolean.class);
    }

    /** Checks if the user has Reddit Gold */
    @JsonProperty
    public Boolean hasGold() {
        return data("is_gold", Boolean.class);
    }

    /** Checks whether this account moderates any subreddits */
    @JsonProperty
    public Boolean isMod() {
        return data("is_mod", Boolean.class);
    }

    /** Gets the user's link karma */
    @JsonProperty
    public Integer getLinkKarma() {
        return data("link_karma", Integer.class);
    }

    /** Checks if this account is said to be over 18 and "willing to view adult content." */
    @JsonProperty(nullable = true)
    public Boolean isOver18() {
        return data("over_18", Boolean.class);
    }

    @Override
    public Date getCreated() {
        return _getCreated();
    }

    @Override
    public Date getCreatedUtc() {
        return _getCreatedUtc();
    }
}
