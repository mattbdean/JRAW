package net.dean.jraw.models;

import org.codehaus.jackson.JsonNode;

/**
 * Represents a comment as shown in a response by {@code POST /api/morechildren}.
 */
public class CompactComment extends Thing {
    /**
     * Instantiates a new CompactComment
     *
     * @param dataNode The node to parse data from
     */
    public CompactComment(JsonNode dataNode) {
        super(dataNode);
    }

    /**
     * Gets the body of the comment
     */
    @JsonProperty
    public RenderStringPair getContent() {
        return new RenderStringPair(data("contentText"), data("contentHTML"));
    }

    /**
     * Gets the HTML that will be appended to the HTML UI on the website
     */
    @JsonProperty
    public String getFullHtml() {
        return data("content");
    }

    /**
     * Gets the fullname of the submission this comment was posted in
     */
    @JsonProperty
    public String getParentSubmission() {
        return data("link");
    }

    /**
     * Gets the fullname of this comment's parent
     */
    @JsonProperty
    public String getParentComment() {
        return data("parent");
    }

    @Override
    public ThingType getType() {
        // Not technically a "comment", but "kind" == "t1"
        return ThingType.COMMENT;
    }
}
