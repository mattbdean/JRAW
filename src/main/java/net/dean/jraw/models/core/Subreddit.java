package net.dean.jraw.models.core;

import net.dean.jraw.models.JsonInteraction;
import net.dean.jraw.models.RenderStringPair;
import net.dean.jraw.models.ThingType;
import org.codehaus.jackson.JsonNode;

import java.awt.*;
import java.net.URI;
import java.net.URL;

/**
 * This class represents a Subreddit such as /r/pics.
 */
public class Subreddit extends Thing {

    /**
     * Instantiates a new Subreddit
     *
     * @param dataNode The node to parse data from
     */
    public Subreddit(JsonNode dataNode) {
        super(dataNode);
    }

    /**
     * Gets the amount of users active in the last 15 minutes
     * @return The number of active users
     */
    @JsonInteraction
    public Integer getAccountsActive() {
        return data("accounts_active", Integer.class);
    }

    /**
     * Gets the number of minutes the subreddit initially hides comment scores
     * @return The number of minutes the subreddit initially hides comment scores
     */
    @JsonInteraction
    public Integer getCommentScoreHideDuration() {
        return data("comment_score_hide_mins", Integer.class);
    }

    /**
     * Gets the subreddit's description
     * @return The subreddit's description
     */
    @JsonInteraction
    public RenderStringPair getSidebar() {
        return new RenderStringPair(data("description"), data("description_html"));
    }

    /**
     * Gets the "human" name of the subreddit (ex: "pics")
     * @return The subreddit's name
     */
    @JsonInteraction
    public String getDisplayName() {
        return data("display_name");
    }

    /**
     * Gets the full URL to the header image, or null if one is not present.
     * @return The full URL to the header image
     */
    @JsonInteraction(nullable = true)
    public URL getHeaderImage() {
        return data("header_img", URL.class);
    }

    /**
     * Gets the dimensions of the header image, or null if the header does not exist
     * @return The dimensions of the header image
     */
    @JsonInteraction(nullable = true)
    public Dimension getHeaderSize() {
        JsonNode node = data.get("header_size");
        if (node.isNull()) {
            return null;
        }
        return new Dimension(node.get(0).asInt(-1), node.get(1).asInt(-1));
    }

    /**
     * Gets the description of the header image shown when the cursor has hovered over it, or null if a header image is
     * not present
     * @return The header image's description
     */
    @JsonInteraction(nullable = true)
    public String getHeaderTitle() {
        return data("header_title");
    }

    /**
     * Checks if this subreddit is not safe for work
     * @return If this subreddit is NSFW
     */
    @JsonInteraction
    public Boolean isNsfw() {
        return data("over18", Boolean.class);
    }

    /**
     * Gets the public description show in the subreddit search results
     * @return The public description
     */
    @JsonInteraction
    public String getPublicDescription() {
        return data("public_description");
    }

    /**
     * Checks if the subreddit's traffic page is publicly accessible
     * @return If the subreddit's traffic page is publicly accessible
     */
    @JsonInteraction
    public Boolean isTrafficPublic() {
        return data("public_traffic", Boolean.class);
    }

    /**
     * Gets the amount of users subscribed to this subreddit
     * @return The amount of users subscribed to this subreddit
     */
    @JsonInteraction
    public Long getSubscriberCount() {
        return data("subscribers", Long.class);
    }

    /**
     * Checks if this subreddit allows self (text) posts
     * @return If this subreddit allows self posts
     */
    @JsonInteraction
    public Boolean isAllowingSelfPosts() {
        String type = data("submission_type");
        return type.equals("self") || type.equals("all");
    }

    /**
     * Checks if this subreddit allows link posts
     * @return If this subreddit allows link posts
     */
    @JsonInteraction
    public Boolean isAllowingLinks() {
        String type = data("submission_type");
        return type.equals("link") || type.equals("all");
    }

    /**
     * Gets the subreddit's custom label for the "submit link" button, if any
     * @return The subreddit's custom label for the "submit link" button
     */
    @JsonInteraction
    public String getSubmitLinkLabel() {
        return data("submit_link_label");
    }

    /**
     * Gets the subreddit's custom label for the "submit text" button, if any
     * @return The subreddit's custom label for the "submit link" button
     */
    @JsonInteraction
    public String getSubmitTextLabel() {
        return data("submit_text_label");
    }

    /**
     * Gets this subreddit's type
     * @return This subreddit's type
     */
    @JsonInteraction
    public Type getSubredditType() {
        return Type.valueOf(data("subreddit_type").toUpperCase());
    }

    /**
     * Gets the title of the main page
     * @return The title of the main page
     */
    @JsonInteraction
    public String getTitle() {
        return data("title");
    }

    /**
     * Gets the relative URL of the subreddit (ex: "/r/pics")
     * @return The relative URL of the subreddit
     */
    @JsonInteraction
    public URI getRelativeLocation() {
        return data("url", URI.class);
    }

    /**
     * Checks if the logged-in-user is banned from this subreddit
     * @return If the logged-in-user is banned form this subreddit
     */
    @JsonInteraction
    public Boolean isUserBanned() {
        return data("user_is_banned", Boolean.class);
    }

    /**
     * Checks if the logged-in-user contributes to this subreddit
     * @return If the logged-in-user contributes to this subreddit
     */
    @JsonInteraction
    public Boolean isUserContributor() {
        return data("user_is_contributor", Boolean.class);
    }

    /**
     * Checks if the logged-in-user is a moderator of this subreddit
     * @return If the logged-in-user is a moderator of this subreddit
     */
    @JsonInteraction
    public Boolean isUserModerator() {
        return data("user_is_moderator", Boolean.class);
    }

    /**
     * Checks if the logged-in-user is subscribed to this subreddit
     * @return If the logged-in-user is subscribed to this subreddit
     */
    @JsonInteraction
    public Boolean isUserSubscriber() {
        return data("user_is_subscriber", Boolean.class);
    }

    /**
     * This class represents a list of all the available subreddit types
     */
    public static enum Type {
        /** Open to all users */
        PUBLIC,
        /** only approved members can view and submit */
        PRIVATE,
        /** Anyone can view, but only some are approved to submit links */
        RESTRICTED,
        /** Only users with Reddit gold can post (?) */
        GOLD_RESTRICTED,
        ARCHIVED
    }

    @Override
    public ThingType getType() {
        return ThingType.SUBREDDIT;
    }
}
