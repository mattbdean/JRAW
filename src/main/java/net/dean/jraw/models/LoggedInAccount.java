package net.dean.jraw.models;

import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents an account with additional information visible only to the logged-in user.
 */
@Model(kind = Model.Kind.ACCOUNT)
public class LoggedInAccount extends Account {
    /** Instantiates a new LoggedInAccount */
    public LoggedInAccount(JsonNode data) {
        super(data);
    }

    /** Checks if the user has unread mail. */
    @JsonProperty
    public Boolean hasMail() {
        return data("has_mail", Boolean.class);
    }

    /** Checks if the user has moderator mail */
    @JsonProperty(nullable = true)
    public Boolean hasModMail() {
        return data("has_mod_mail", Boolean.class);
    }

    /** Checks if the user has a verified email */
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
