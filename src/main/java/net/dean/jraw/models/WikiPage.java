package net.dean.jraw.models;

import net.dean.jraw.models.core.Account;
import org.codehaus.jackson.JsonNode;

import java.util.Date;

/**
 * Represents a wiki page. See <a href="http://www.reddit.com/wiki/wiki">here</a> for some basic information
 */
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
     * @return If the current user can edit this page
     */
    @JsonInteraction
    public Boolean mayRevise() {
        return data("may_revise", Boolean.class);
    }

    /**
     * Gets the date of last revision (or creation?)
     * @return The date of last revision
     */
    @JsonInteraction
    public Date getRevisionDate() {
        return data("revision_date", Date.class);
    }

    /**
     * Gets the content of this page
     * @return The content
     */
    @JsonInteraction
    public RenderStringPair getContent() {
        return new RenderStringPair(data("content_md"), data("content_html"));
    }

    /**
     * Gets the person who last revised this page
     * @return The person ({@link net.dean.jraw.models.core.Account}) who last revised this page
     */
    @JsonInteraction
    public Account getRevisionBy() {
        return new Account(data.get("revision_by").get("data"));
    }

    @Override
    public ThingType getType() {
        return ThingType.WIKI_PAGE;
    }
}
