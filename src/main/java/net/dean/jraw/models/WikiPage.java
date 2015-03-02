package net.dean.jraw.models;

import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Date;

/** This class represents a Reddit-hosted wiki page. */
@Model(kind = Model.Kind.WIKI_PAGE)
public class WikiPage extends RedditObject {
    /** Instantiates a new WikiPage */
    public WikiPage(JsonNode dataNode) {
        super(dataNode);
    }

    /** Checks if the current user can edit this page */
    @JsonProperty
    public Boolean mayRevise() {
        return data("may_revise", Boolean.class);
    }

    /** Gets the date of last revision. If there have been no revisions, then the date of creation is returned. */
    @JsonProperty
    public Date getRevisionDate() {
        return data("revision_date", Date.class);
    }

    /** Gets the content of this page */
    @JsonProperty
    public String getContent() {
        return data("content_md");
    }

    /** Gets the person who last revised this page */
    @JsonProperty(nullable = true)
    public Account getCurrentRevisionAuthor() {
        if (data.get("revision_by").isNull()) {
            return null;
        }
        return new Account(data.get("revision_by").get("data"));
    }
}
