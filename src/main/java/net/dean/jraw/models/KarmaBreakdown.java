package net.dean.jraw.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import net.dean.jraw.managers.AccountManager;
import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;

import java.util.Map;

/**
 * Represents a breakdown of link and comment karma by subreddit. Only accessible with OAuth2
 *
 * @see AccountManager#getKarmaBreakdown()
 */
@Model(kind = Model.Kind.KARMA_BREAKDOWN)
public class KarmaBreakdown extends RedditObject {
    private final Map<String, SubredditKarma> summaries;

    /**
     * Instantiates a new KarmaBreakdown. Unlike most other JsonModels, the properties of this class are stored in
     * fields which are parsed in the constructor.
     *
     * @param dataNode The node to parse data from
     */
    public KarmaBreakdown(JsonNode dataNode) {
        super(dataNode);
        ImmutableMap.Builder<String, SubredditKarma> list = ImmutableMap.builder();
        for (JsonNode summaryNode : data) {
            list.put(summaryNode.get("sr").asText(), new SubredditKarma(summaryNode));
        }

        this.summaries = list.build();
    }

    /**
     * Gets a list of summaries that makes up the core of this breakdown
     */
    @JsonProperty
    public Map<String, SubredditKarma> getSummaries() {
        return summaries;
    }

    /** Gets the amount of link karma in the given subreddit */
    public int getLinkKarma(String subreddit) {
        return getNotNullSubredditKarma(subreddit).link();
    }

    /** Gets the amount of comment karma in the given subreddit */
    public int getCommentKarma(String subreddit) {
        return getNotNullSubredditKarma(subreddit).comment();
    }

    private SubredditKarma getNotNullSubredditKarma(String subreddit) {
        SubredditKarma karma = summaries.get(subreddit);
        if (karma == null)
            throw new IllegalArgumentException("No karma in /r/" + subreddit);
        return karma;
    }

    /** Represents the link and comment karma in a subreddit. */
    public static class SubredditKarma extends JsonModel {
        private Integer link;
        private Integer comment;
        public SubredditKarma(JsonNode dataNode) {
            super(dataNode);
            this.link = data("link_karma", Integer.class);
            this.comment = data("comment_karma", Integer.class);
        }

        @JsonProperty
        /** Gets the amount of link karma in the given subreddit */
        public Integer link() {
            return link;
        }

        @JsonProperty
        /** Gets the amount of comment karma in the given subreddit */
        public Integer comment() {
            return comment;
        }
    }
}
