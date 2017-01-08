package net.dean.jraw.models;

import com.fasterxml.jackson.databind.JsonNode;
import net.dean.jraw.models.attr.Created;
import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a live thread. See <a href="https://www.reddit.com/r/live/wiki/index">here</a> for more information.
 */
@Model(kind = Model.Kind.LIVE_THREAD)
public final class LiveThread extends RedditObject implements Created {
    /**
     * Instantiates a new LiveEvent
     *
     * @param dataNode The node to parse data from
     */
    public LiveThread(JsonNode dataNode) {
        super(dataNode);
    }

    /** Gets the thread's description */
    @JsonProperty
    public String getDescription() {
        return data("description");
    }

    /** Gets the thread's title */
    @JsonProperty
    public String getTitle() {
        return data("title");
    }

    /**
     * Gets the WebSocket URL (wss://) to the thread. WebSocket clients can use this URL to be notified of new
     * updates.
     */
    @JsonProperty(nullable = true)
    public String getWebsocketUrl() {
        return data("websocket_url");
    }

    /** Gets this thread's state. Either "live" or "complete." */
    @JsonProperty
    public Boolean getState() {
        return data("state").equals("live");
    }

    /** Gets the amount of people watching this thread */
    @JsonProperty
    public Integer getViewerCount() {
        return data("viewer_count", Integer.class);
    }

    /** Gets the localized amount of people watching this thread */
    public String getLocalizedViewerCount() {
        try {
            return NumberFormat.getInstance().format(getViewerCount());
        } catch (final IllegalArgumentException ex) {
            return null;
        }
    }

    /** Checks if the viewer count is "fuzzed". This most often happens when there are less than 100 viewers. */
    @JsonProperty
    public Boolean isViewerCountFuzzed() {
        return data("viewer_count_fuzzed", Boolean.class);
    }

    /** This LiveEvent's ID. Not to be confused with {@link Thing#getId()}. */
    @JsonProperty
    public String getId() {
        return data("id");
    }

    /** Any additional information provided by the updaters for the viewers' benefit. */
    @JsonProperty
    public String getResources() {
        return data("resources");
    }

    @Override
    public Date getCreated() {
        return _getCreated();
    }

    @Model(kind = Model.Kind.NONE)
    public static class Contributor extends Thing {

        /** Instantiates a new Contributor */
        public Contributor(JsonNode dataNode) {
            super(dataNode);
        }

        @JsonProperty
        public List<String> getPermissions() {
            List<String> perms = new ArrayList<>();
            for (JsonNode node : data.get("permissions")) {
                perms.add(node.asText());
            }
            return perms;
        }
    }
}
