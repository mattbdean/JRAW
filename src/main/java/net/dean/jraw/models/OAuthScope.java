package net.dean.jraw.models;

import com.fasterxml.jackson.databind.JsonNode;
import net.dean.jraw.models.meta.JsonProperty;

public class OAuthScope extends Thing {
    public OAuthScope(JsonNode dataNode) {
        super(dataNode);
    }

    @JsonProperty
    public String getDescription() {
        return data("description");
    }
}
