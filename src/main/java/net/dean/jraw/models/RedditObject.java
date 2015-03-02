package net.dean.jraw.models;

import com.fasterxml.jackson.databind.JsonNode;
import net.dean.jraw.Dimension;

import java.util.Date;

/**
 * A RedditObject represents an abstract data structure presented by the Reddit API. Its most notable subclass is
 * {@link Thing}, from which all common Reddit-based models are derived.
 */
public abstract class RedditObject extends JsonModel {

    /**
     * Instantiates a new RedditObject
     *
     * @param dataNode The node to parse data from
     */
    public RedditObject(JsonNode dataNode) {
        super(dataNode);
    }

    /**
     * Gets the date this object was created in local time
     * @return Date created in local time
     */
    protected final Date _getCreated() {
        // created in seconds, Date constructor wants milliseconds
        return new Date(getDataNode().get("created").longValue() * 1000);
    }

    /**
     * Gets the date this object was created in UTC
     * @return Date created in UTC
     */
    protected final Date _getCreatedUtc() {
        // created in seconds, Date constructor wants milliseconds
        return new Date(getDataNode().get("created_utc").longValue() * 1000);
    }

    protected final DistinguishedStatus _getDistinguishedStatus() {
        String distinguished = getDataNode().get("distinguished").textValue();

        if (distinguished == null) {
            return DistinguishedStatus.NORMAL;
        }

        return DistinguishedStatus.getByJsonValue(distinguished);
    }

    protected final Integer _getTimesGilded() {
        if (!getDataNode().has("gilded")) {
            return 0;
        }
        return getDataNode().get("gilded").asInt();
    }

    protected final Integer _getScore() {
        return getDataNode().get("score").intValue();
    }

    protected final VoteDirection _getVote() {
        JsonNode likes = getDataNode().get("likes");
        if (likes.isNull()) {
            return VoteDirection.NO_VOTE;
        }

        return likes.booleanValue() ? VoteDirection.UPVOTE : VoteDirection.DOWNVOTE;
    }

    protected final Dimension _getDimension(String jsonKey) {
        JsonNode node = data.get(jsonKey);
        if (node == null || node.isNull())
            return null;
        return new Dimension(node.get(0).asInt(-1), node.get(1).asInt(-1));
    }

    protected final Dimension _getHeaderSize() {
        return _getDimension("header_size");
    }
}
