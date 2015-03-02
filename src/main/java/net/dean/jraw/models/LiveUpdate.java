package net.dean.jraw.models;

import com.google.common.collect.ImmutableList;
import net.dean.jraw.models.attr.Created;
import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Date;

/**
 * Represents an update in a live thread
 */
@Model(kind = Model.Kind.LIVE_UPDATE)
public class LiveUpdate extends Thing implements Created {
    /** Instantiates a new LiveUpdate */
    public LiveUpdate(JsonNode dataNode) {
        super(dataNode);
    }

    /** The value of the update */
    @JsonProperty
    public String getBody() {
        return data("body");
    }

    /** Gets the name of the user who created this update */
    @JsonProperty
    public String getAuthor() {
        return data("author");
    }

    /** Checks if this update has been marked incorrect or crossed out. */
    @JsonProperty
    public Boolean isStricken() {
        return data("stricken", Boolean.class);
    }

    /**
     * Gets the Embeds found in this LiveUpdate. Will most likely only have zero or one element in the list.
     * @return A list of Embeds
     */
    @JsonProperty
    public ImmutableList<Embed> getEmbeds() {
        ImmutableList.Builder<Embed> builder = ImmutableList.builder();
        for (JsonNode embedNode : data.get("embeds")) {
            builder.add(new Embed(embedNode));
        }
        return builder.build();
    }

    @Override
    public Date getCreated() {
        return _getCreated();
    }

    @Override
    public Date getCreatedUtc() {
        return _getCreatedUtc();
    }

    /** Represents embedded data in a LiveUpdate */
    public static class Embed extends JsonModel {
        public Embed(JsonNode dataNode) {
            super(dataNode);
        }

        @JsonProperty
        public String getUrl() {
            return data("url");
        }

        @JsonProperty
        public Integer getWidth() {
            return data("width", Integer.class);
        }

        @JsonProperty
        public Integer getHeight() {
            return data("height", Integer.class);
        }
    }
}
