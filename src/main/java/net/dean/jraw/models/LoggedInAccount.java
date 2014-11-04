package net.dean.jraw.models;

import org.codehaus.jackson.JsonNode;

/**
 * Represents a the authenticated user's account
 */
public class LoggedInAccount extends Account {
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

    /**
     * Gets the current modhash
     * @return Current modhash, or null if not your account
     */
    @JsonProperty(nullable = true)
    public String getModHash() {
        return data("modhash");
    }
}
