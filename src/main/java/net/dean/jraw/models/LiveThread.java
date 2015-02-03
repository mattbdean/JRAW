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

    /**
     * Gets the thread's description
     * @return The thread's description
     */
    @JsonProperty
    public String getDescription() {
        return data("description");
    }

    /**
     * Gets the title of the thread
     * @return The title of the thread
     */
    @JsonProperty
    public String getTitle() {
        return data("title");
    }

    /**
     * Returns the WebSocket URL (wss://) to the thread
     * @return The WebSocket URL
     */
    @JsonProperty(nullable = true)
    public String getWebsocketUrl() {
        return data("websocket_url");
    }

    /**
     * Checks if this live event is still active
     * @return If this event is still active
     */
    @JsonProperty
    public Boolean isActive() {
        return data("state").equals("live"); // 'complete' if not active
    }

    /**
     * Gets the amount of people watching this thread
     * @return The amount of viewers
     */
    @JsonProperty
    public Integer getViewerCount() {
        return data("viewer_count", Integer.class);
    }

    /**
     * Checks if the viewer count is "fuzzed". This most often happens when there are less than 100 viewers.
     * @return If the viewer count is fuzzed
     */
    @JsonProperty
    public Boolean isViewerCountFuzzed() {
        return data("viewer_count_fuzzed", Boolean.class);
    }

    /**
     * This LiveEvent's ID. Do not confuse this with {@link Thing#getId()}.
     * @return The thread's ID
     */
    @JsonProperty
    public String getId() {
        return data("id");
    }

    /**
     * Gets the data under the "resources" header displayed on the right side of the Reddit Live page.
     * @return The resources
     */
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
