package net.dean.jraw.models;

import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the settings of a wiki page
 */
@Model(kind = Model.Kind.WIKI_PAGE_SETTINGS)
public class WikiPageSettings extends RedditObject {

    /**
     * Instantiates a new RedditObject
     *
     * @param dataNode The node to parse data from
     */
    public WikiPageSettings(JsonNode dataNode) {
        super(dataNode);
    }

    /**
     * Get the approved editors of this wiki page.
     *
     * @return The approved editors
     */
    @JsonProperty
    public List<Account> getEditors() {
        JsonNode editors = data.get("editors");
        List<Account> editorsList = new ArrayList<>(editors.size());
        for (JsonNode editor : editors) {
            editorsList.add(new Account(editor.get("data")));
        }

        return editorsList;
    }

    /**
     * Checks if this wiki page is shown in the index of pages
     *
     * @return If this page is listed
     */
    @JsonProperty
    public Boolean isListed() {
        return data("listed", Boolean.class);
    }

    /**
     * Returns an integer from 0 to 2 inclusive that represents the permissions of that page. A permlevel of 0 means
     * that this wiki page is following the default subreddit permissions, 1 means that on approved wiki contributors
     * for this page may edit, and 2 means only mods may edit and view.
     *
     * @return An integer from 0 to 2 inclusive
     */
    @JsonProperty
    public Integer getPermLevel() {
        return data("permlevel", Integer.class);
    }
}
