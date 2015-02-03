package net.dean.jraw.models;

import com.google.common.collect.ImmutableList;
import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Represents a breakdown of link and comment karma by subreddit. Only accessible with OAuth2
 */
@Model(kind = Model.Kind.KARMA_BREAKDOWN)
public class KarmaBreakdown extends RedditObject {
    /**
     * Instantiates a new KarmaBreakdown
     *
     * @param dataNode The node to parse data from
     */
    public KarmaBreakdown(JsonNode dataNode) {
        super(dataNode);
    }

    /**
     * Gets a list of summaries that makes up the core of this breakdown
     */
    @JsonProperty
    public List<Summary> getSummaries() {
        ImmutableList.Builder<Summary> list = ImmutableList.builder();
        for (JsonNode summary : data) {
            list.add(new Summary(summary));
        }

        return list.build();
    }

    /**
     * Represents one subreddit in a karma breakdown
     */
    public static class Summary extends JsonModel {

        /**
         * Instantiates a new Summary
         *
         * @param dataNode The node to parse data from
         */
        public Summary(JsonNode dataNode) {
            super(dataNode);
        }

        @JsonProperty
        public String getSubreddit() {
            return data("sr");
        }

        @JsonProperty
        public Integer getLinkKarma() {
            return data("link_karma", Integer.class);
        }

        @JsonProperty
        public Integer getCommentKarma() {
            return data("comment_karma", Integer.class);
        }
    }
}
