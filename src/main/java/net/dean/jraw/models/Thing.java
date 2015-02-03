package net.dean.jraw.models;

import net.dean.jraw.managers.ThingCache;
import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents the base class of all objects defined in the Reddit API, except for "Listing" and "more". More information
 * is available <a href="https://github.com/reddit/reddit/wiki/JSON#thing-reddit-base-class">here</a>.
 *
 * @author Matthew Dean
 */
@Model(kind = Model.Kind.ABSTRACT)
public abstract class Thing extends RedditObject {

    /**
     * Instantiates and registers a new Thing
     *
     * @param dataNode The node to parse data from
     */
    public Thing(JsonNode dataNode) {
        super(dataNode);
        ThingCache.instance().addThing(this);
    }

    /**
     * Gets this Thing's full identifier, e.g. "8xwlg"
     * @return This Thing's full identifier
     */
    @JsonProperty
    public String getId() {
        return data("id");
    }

    /**
     * Gets the full name of this Thing, e.g. "t1_c3v7f8u"
     * @return This Thing's full name
     */
    @JsonProperty
    public String getFullName() {
        return data("name");
    }
}
