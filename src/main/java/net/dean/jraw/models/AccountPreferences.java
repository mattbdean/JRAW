package net.dean.jraw.models;

import net.dean.jraw.models.meta.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class represents an account's preferences.
 */
public class AccountPreferences extends JsonModel {
    /** Instantiates a new AccountPreferences */
    public AccountPreferences(JsonNode dataNode) {
        super(dataNode);
    }

    /** Gets the language that Reddit will use in its interface. For example, "en", "en-us", "fr" */
    @JsonProperty(nullable = true)
    public String getLanguage() {
        return data("lang");
    }

    /** Gets whether the user uses the Reddit toolbar to look at links on the website */
    @JsonProperty(nullable = true)
    public Boolean isUsingToolbar() {
        return data("frame", Boolean.class);
    }

    /** Gets whether links will open in a new tab (left click will function as a middle click) */
    @JsonProperty(nullable = true)
    public Boolean isUsingNewWindow() {
        return data("newwindow", Boolean.class);
    }

    /** Gets the user's preference on how thumbnails should be displayed */
    @JsonProperty(nullable = true)
    public ThumbnailDisplayPreference getThumbnailDisplayPreference() {
        return ThumbnailDisplayPreference.valueOf(data("media").toUpperCase());
    }

    /** Checks if the user wants the thumbnails to not be displayed next to NSFW links */
    @JsonProperty(nullable = true)
    public Boolean isHidingNsfwThumbs() {
        return data("no_profanity", Boolean.class);
    }

    /** Checks if the spotlight box shows on the front page */
    @JsonProperty(nullable = true)
    public Boolean isShowingSpotlightBox() {
        return data("organic", Boolean.class);
    }

    /** Checks if trending subreddits will appear on the front page */
    @JsonProperty(nullable = true)
    public Boolean isShowingTrending() {
        return data("show_trending", Boolean.class);
    }

    /** Checks if recently clicked links will be shown */
    @JsonProperty(nullable = true)
    public Boolean isShowingRecentlyViewedLinks() {
        return data("clickgadget", Boolean.class);
    }

    /** Checks if the posts shown on the website will show in a 'compressed' fashion. */
    @JsonProperty(nullable = true)
    public Boolean isCompressingLinks() {
        return data("compress", Boolean.class);
    }

    /** Checks if additional information about the domain of a submission will be shown */
    @JsonProperty(nullable = true)
    public Boolean isShowingDomainDetails() {
        return data("domain_details", Boolean.class);
    }

    /** Checks if posts (except for your own) will be shown after they have been upvoted */
    @JsonProperty(nullable = true)
    public Boolean isHidingUpvotedPosts() {
        return data("hide_ups", Boolean.class);
    }

    /** Checks if posts (except for your own) will be shown after they have been downvoted */
    @JsonProperty(nullable = true)
    public Boolean isHidingDownvotedPosts() {
        return data("hide_downs", Boolean.class);
    }

    /** Gets the amount of submissions that will be displayed on each page */
    @JsonProperty(nullable = true)
    public Integer getPostsPerPage() {
        return data("numsites", Integer.class);
    }

    /** Gets the minimum submission score required to be shown to the user. Will be null to show all links */
    @JsonProperty(nullable = true)
    public Integer getLinkScoreThreshold() {
        if (!data.has("min_link_score")) {
            return null;
        }
        return data("min_link_score", Integer.class);
    }

    /** Gets the minimum comment score required to be shown to the user. Will be null to show all comments */
    @JsonProperty(nullable = true)
    public Integer getCommentScoreThreshold() {
        if (!data.has("min_comment_score")) {
            return null;
        }
        return data("min_comment_score", Integer.class);
    }

    /** Gets the amount of comments to display when loading a submission */
    @JsonProperty(nullable = true)
    public Integer getDefaultCommentCount() {
        return data("num_comments", Integer.class);
    }

    /** Checks if a dagger (†) will be shown next to comments voted controversial */
    @JsonProperty(nullable = true)
    public Boolean isHighlightingControversial() {
        return data("highlight_controversial", Boolean.class);
    }

    /** Checks if message conversations will be shown in the inbox */
    @JsonProperty(nullable = true)
    public Boolean isShowingPmThreads() {
        return data("threaded_messages", Boolean.class);
    }

    /** Checks if messages will be automatically collapsed after reading them */
    @JsonProperty(nullable = true)
    public Boolean isMessageAutoCollapse() {
        return data("collapse_read_messages", Boolean.class);
    }

    /** Checks if all unread messages will automatically be marked as read once the inbox is opened */
    @JsonProperty(nullable = true)
    public Boolean isAutoReadMessages() {
        return data("mark_messages_read", Boolean.class);
    }

    /** Checks if custom stylesheets will be used on subreddits */
    @JsonProperty(nullable = true)
    public Boolean isUsingCustomStylesheets() {
        return data("show_stylesheets", Boolean.class);
    }

    /** Checks if user flair will be shown */
    @JsonProperty(nullable = true)
    public Boolean isShowingUserFlair() {
        return data("show_flair", Boolean.class);
    }

    /** Checks if link flair will be shown */
    @JsonProperty(nullable = true)
    public Boolean isShowingLinkFlair() {
        return data("show_link_flair", Boolean.class);
    }

    /** Checks if the user is "over eighteen years old and willing to view adult content" */
    @JsonProperty(nullable = true)
    public Boolean isOver18() {
        return data("over_18", Boolean.class);
    }

    /** Checks if posts will be labeled 'NSFW' if they are not safe for work */
    @JsonProperty(nullable = true)
    public Boolean isLabelingNsfwPosts() {
        return data("label_nsfw", Boolean.class);
    }

    /** Checks if private RSS feeds are enabled */
    @JsonProperty(nullable = true)
    public Boolean isPrivateRssEnabled() {
        return data("private_feeds", Boolean.class);
    }

    /** Checks if the user's upvoted/downvoted links/comments are public */
    @JsonProperty(nullable = true)
    public Boolean isVoteHistoryPublic() {
        return data("public_votes", Boolean.class);
    }

    /** Checks if the user has given consent for their account to be used in research */
    @JsonProperty(nullable = true)
    public Boolean isResearchable() {
        return data("research", Boolean.class);
    }

    /** Checks if the user's profile will be hidden from search engines */
    @JsonProperty(nullable = true)
    public Boolean isHiddenFromSearchEngines() {
        return data("hide_from_robots", Boolean.class);
    }
}
