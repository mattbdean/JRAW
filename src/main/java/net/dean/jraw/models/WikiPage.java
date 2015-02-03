package net.dean.jraw.models;

import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Date;

/**
 * Represents a wiki page. See <a href="http://www.reddit.com/wiki/wiki">here</a> for some basic information
 */
@Model(kind = Model.Kind.WIKI_PAGE)
public class WikiPage extends RedditObject {
    /**
     * Instantiates a new WikiPage
     *
     * @param dataNode The node to parse data from
     */
    public WikiPage(JsonNode dataNode) {
        super(dataNode);
    }

    /**
     * Checks if the current user can edit this page
     *
     * @return If the current user can edit this page
     */
    @JsonProperty
    public Boolean mayRevise() {
        return data("may_revise", Boolean.class);
    }

    /**
     * Gets the date of last revision (or creation?)
     *
     * @return The date of last revision
     */
    @JsonProperty
    public Date getRevisionDate() {
        return data("revision_date", Date.class);
    }

    /**
     * Gets the content of this page
     *
     * @return The content
     */
    @JsonProperty
    public String getContent() {
        return data("content_md");
    }

    /**
     * Gets the person who last revised this page
     *
     * @return The person ({@link Account}) who last revised this page
     */
    @JsonProperty(nullable = true)
    public Account getCurrentRevisionAuthor() {
        if (data.get("revision_by").isNull()) {
            return null;
        }
        return new Account(data.get("revision_by").get("data"));
    }
}
