package net.dean.jraw.models;

import net.dean.jraw.Dimension;
import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

/** This class represents a subreddit, such as /r/pics. */
@Model(kind = Model.Kind.SUBREDDIT)
public final class Subreddit extends Thing implements Comparable<Subreddit> {

    /** Instantiates a new Subreddit */
    public Subreddit(JsonNode dataNode) {
        super(dataNode);
    }

    /** Gets the amount of active users this subreddit has seen in the last 15 minutes */
    @JsonProperty
    public Integer getAccountsActive() {
        return data("accounts_active", Integer.class);
    }

    /** Gets the number of minutes the subreddit will initially hide comment scores for */
    @JsonProperty
    public Integer getCommentScoreHideDuration() {
        return data("comment_score_hide_mins", Integer.class);
    }

    /**
     * Gets the subreddit's description. This appears on the sidebar on the website.
     */
    @JsonProperty
    public String getSidebar() {
        return data("description");
    }

    /** Gets the "human readable" name of the subreddit (ex: "pics") */
    @JsonProperty
    public String getDisplayName() {
        return data("display_name");
    }

    /** Gets the full URL to the header image, or null if one is not present. */
    @JsonProperty(nullable = true)
    public String getHeaderImage() {
        return data("header_img");
    }

    /** Gets the dimensions of the header image, or null if the header does not exist */
    @JsonProperty(nullable = true)
    public Dimension getHeaderSize() {
        return _getHeaderSize();
    }

    /**
     * Gets the description of the header image shown when the cursor has hovered over it, or null if a header image is
     * not present
     */
    @JsonProperty(nullable = true)
    public String getHeaderTitle() {
        return data("header_title");
    }

    /** Checks if this subreddit is not safe for work */
    @JsonProperty
    public Boolean isNsfw() {
        return data("over18", Boolean.class);
    }

    /** Gets the information that will show when this subreddit appears in a search */
    @JsonProperty
    public String getPublicDescription() {
        return data("public_description");
    }

    /** Checks if the subreddit's traffic page is publicly accessible */
    @JsonProperty
    public Boolean isTrafficPublic() {
        return data("public_traffic", Boolean.class);
    }

    /** Gets the amount of users subscribed to this subreddit */
    @JsonProperty
    public Long getSubscriberCount() {
        return data("subscribers", Long.class);
    }

    /** Gets the types of submissions allowed to be posted on this subreddit */
    @JsonProperty
    public SubmissionType getAllowedSubmissionType() {
        JsonNode submissionType = data.get("submission_type");
        if (submissionType.isNull()) {
            return SubmissionType.NONE;
        }
        return SubmissionType.valueOf(submissionType.asText().toUpperCase());
    }

    /** Gets the subreddit's custom label for the "submit link" button, if any. */
    @JsonProperty
    public String getSubmitLinkLabel() {
        return data("submit_link_label");
    }

    /** Gets the subreddit's custom label for the "submit text" button, if any */
    @JsonProperty
    public String getSubmitTextLabel() {
        return data("submit_text_label");
    }

    /** Gets this subreddit's traffic restriction type */
    @JsonProperty
    public Type getSubredditType() {
        return Type.valueOf(data("subreddit_type").toUpperCase());
    }

    @JsonProperty
    public String getTitle() {
        return data("title");
    }

    /** Gets the relative URL of the subreddit (ex: "/r/pics") */
    @JsonProperty
    public String getRelativeLocation() {
        return data("url");
    }

    /** Checks if the logged-in user is banned from this subreddit */
    @JsonProperty
    public Boolean isUserBanned() {
        return data("user_is_banned", Boolean.class);
    }

    /** Checks if the logged-in user is an approved contributor for this subreddit */
    @JsonProperty
    public Boolean isUserContributor() {
        return data("user_is_contributor", Boolean.class);
    }

    /** Checks if the logged-in user is a moderator of this subreddit */
    @JsonProperty
    public Boolean isUserModerator() {
        return data("user_is_moderator", Boolean.class);
    }

    /** Checks if the logged-in user is subscribed to this subreddit */
    @JsonProperty
    public Boolean isUserSubscriber() {
        return data("user_is_subscriber", Boolean.class);
    }


    @Override
    public int compareTo(Subreddit subreddit) {
        return getDisplayName().compareToIgnoreCase(subreddit.getDisplayName());
    }

    /** This class represents a list of all the available subreddit types */
    public enum Type {
        /** Open to all users */
        PUBLIC,
        /** Only approved members can view and submit */
        PRIVATE,
        /** Anyone can view, but only some are approved to submit links */
        RESTRICTED,
        /** Only users with reddit gold can post */
        GOLD_RESTRICTED,
        ARCHIVED
    }

    /** An enumeration of how a subreddit can restrict the type of submissions that can be posted */
    public enum SubmissionType {
        /** Links and self posts */
        ANY,
        /** Only links */
        LINK,
        /** Only self posts */
        SELF,
        /** Restricted subreddit */
        NONE
    }
}
