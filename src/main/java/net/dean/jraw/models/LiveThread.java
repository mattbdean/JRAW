package net.dean.jraw.models;

import net.dean.jraw.models.attr.Created;
import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Date;

/**
 * Represents a live thread. See <a href="https://www.reddit.com/r/live/wiki/index">here</a> for more information.
 */
@Model(kind = Model.Kind.LIVE_THREAD)
public class LiveThread extends RedditObject implements Created {
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

    @Override
    public Date getCreatedUtc() {
        return _getCreatedUtc();
    }
}
