package net.dean.jraw.models;

import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents an account with additional information visible only to that user
 */
@Model(kind = Model.Kind.ACCOUNT)
public class LoggedInAccount extends Account {
    /**
     * Instantiates a new LoggedInAccount
     * @param data The node to parse data from
     */
    public LoggedInAccount(JsonNode data) {
        super(data);
    }

    /**
     * Checks if the user has unread mail. Returns null if the currently logged in account is not this one
     * @return User has unread mail? Null if not your account
     */
    @JsonProperty(nullable = true)
    public Boolean hasMail() {
        return data("has_mail", Boolean.class);
    }

    /**
     * Checks if the user has mod mail
     * @return User has unread mod mail?
     */
    @JsonProperty(nullable = true)
    public Boolean hasModMail() {
        return data("has_mod_mail", Boolean.class);
    }

    /**
     * Checks if the user has a verified email
     * @return User has provided an email address and got it verified?
     */
    @JsonProperty
    public Boolean hasVerifiedEmail() {
        return data("has_verified_email", Boolean.class);
    }

    /** Gets the amount of non-moderator mail the user has. */
    @JsonProperty
    public Integer getInboxCount() {
        return data("inbox_count", Integer.class);
    }

    /** Gets the amount of gold creddits (one month worth of Reddit Gold) the user has. */
    @JsonProperty
    public Integer getCreddits() {
        return data("gold_creddits", Integer.class);
    }
}
