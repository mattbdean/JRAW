package net.dean.jraw.models;

import net.dean.jraw.NoSuchEnumConstantException;
import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a wiki page's settings
 */
@Model(kind = Model.Kind.WIKI_PAGE_SETTINGS)
public class WikiPageSettings extends RedditObject {

    /** Instantiates a new RedditObject */
    public WikiPageSettings(JsonNode dataNode) {
        super(dataNode);
    }

    /** Gets the approved editors of this wiki page. */
    @JsonProperty
    public List<Account> getEditors() {
        JsonNode editors = data.get("editors");
        List<Account> editorsList = new ArrayList<>(editors.size());
        for (JsonNode editor : editors) {
            editorsList.add(new Account(editor.get("data")));
        }

        return editorsList;
    }

    /** Checks if this wiki page is shown in the index of pages */
    @JsonProperty
    public Boolean isListed() {
        return data("listed", Boolean.class);
    }

    /** Gets the editing restrictions on this page */
    @JsonProperty
    public PermissionLevel getPermissionLevel() {
        return PermissionLevel.getByJsonValue(data("permlevel", Integer.class));
    }

    public static enum PermissionLevel {
        /** Follows default (subreddit-wiki-wide) permissions */
        DEFAULT(0),
        /** Only approved wiki contributors may edit this page */
        APPROVED_ONLY(1),
        /** Only moderators may edit this page */
        MODERATOR_ONLY(2);

        private int jsonValue;
        private PermissionLevel(int jsonValue) {
            this.jsonValue = jsonValue;
        }

        public static PermissionLevel getByJsonValue(int jsonValue) {
            for (PermissionLevel level : values()) {
                if (level.jsonValue == jsonValue) {
                    return level;
                }
            }

            throw new NoSuchEnumConstantException(PermissionLevel.class, "" + jsonValue);
        }
    }
}
