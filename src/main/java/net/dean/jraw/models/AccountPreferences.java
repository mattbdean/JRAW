package net.dean.jraw.models;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * A simple key-value read-only store for account preferences. A list of keys can be found
 * <a href="https://www.reddit.com/dev/api#GET_api_v1_me_prefs">here</a> and values can be retrieved with
 * {@link #data(String, Class)}.
 */
public final class AccountPreferences extends JsonModel {
    /** Instantiates a new AccountPreferences */
    public AccountPreferences(JsonNode dataNode) {
        super(dataNode);
    }
}
