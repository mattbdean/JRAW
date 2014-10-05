package net.dean.jraw.models;

import org.codehaus.jackson.JsonNode;

/**
 * Provides another layer of abstraction between "normal" models (as seen <a href="http://www.reddit.com/dev/api#fullnames">here</a>)
 * like Submission and "abnormal" models like Listing and More.
 */
public abstract class RedditObject extends JsonModel {

    /**
     * Instantiates a new RedditObject
     *
     * @param dataNode The node to parse data from
     */
    public RedditObject(JsonNode dataNode) {
        super(dataNode);
    }

    /**
     * Gets the type of this RedditObject. Will always be constant for every class. For example, every
     * {@link Account} class will always return {@link net.dean.jraw.models.ThingType#ACCOUNT}.
     *
     * @return The type of this Thing
     */
    @JsonInteraction
    public abstract ThingType getType();
}
