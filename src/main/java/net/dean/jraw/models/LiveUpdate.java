package net.dean.jraw.models;

import com.google.common.collect.ImmutableList;
import net.dean.jraw.models.attr.Created;
import org.codehaus.jackson.JsonNode;

/**
 * Represents an update in a live thread
 */
public class LiveUpdate extends Thing implements Created {
    /**
     * Instantiates a new LiveUpdate
     *
     * @param dataNode The node to parse data from
     */
    public LiveUpdate(JsonNode dataNode) {
        super(dataNode);
    }

    /**
     * The body text of this update
     * @return The body
     */
    @JsonInteraction
    public RenderStringPair getBody() {
        return data("body", RenderStringPair.class);
    }

    /**
     * Gets the name of the user who created this update
     * @return The author
     */
    @JsonInteraction
    public String getAuthor() {
        return data("author");
    }

//    @JsonInteraction public Boolean isStricken() // I have no idea what the 'stricken' key means

    /**
     * Gets the Embeds found in this LiveUpdate. Will most likely only have zero or one element in the list.
     * @return A list of Embeds
     */
    @JsonInteraction
    public ImmutableList<Embed> getEmbeds() {
        ImmutableList.Builder<Embed> builder = ImmutableList.<Embed>builder();
        for (JsonNode embedNode : data.get("embeds")) {
            builder.add(new Embed(embedNode));
        }
        return builder.build();
    }

    @Override
    public ThingType getType() {
        return ThingType.LIVE_UPDATE;
    }

    /**
     * Represents embedded data in a LiveUpdate
     */
    public static class Embed extends JsonModel {

        /**
         * Instantiates a new JsonModel
         *
         * @param dataNode The node to parse data from
         */
        public Embed(JsonNode dataNode) {
            super(dataNode);
        }

        @JsonInteraction
        public String getUrl() {
            return data("url");
        }

        @JsonInteraction
        public Integer getWidth() {
            return data("width", Integer.class);
        }

        @JsonInteraction
        public Integer getHeight() {
            return data("height", Integer.class);
        }
    }
}
