package net.dean.jraw.models;

import net.dean.jraw.JrawConfig;
import net.dean.jraw.models.core.Account;
import org.codehaus.jackson.JsonNode;

import java.util.Date;

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
     * @return
     */
    @JsonInteraction
    public Date getRevisionDate() {
        return data("revision_date", Date.class);
    }

    @JsonInteraction
    public RenderStringPair getContent() {
        String md = data("content_md");

        if (!JrawConfig.loadRenderStringPairHtml) {
            return new RenderStringPair(md);
        }

        return new RenderStringPair(md, data("content_html"));
    }

    @JsonInteraction
    public Account getRevisionBy() {
        return new Account(data.get("revision_by").get("data"));
    }

    @Override
    public ThingType getType() {
        return ThingType.WIKI_PAGE;
    }
}
