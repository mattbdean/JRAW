package net.dean.jraw;

import com.fasterxml.jackson.databind.JsonNode;
import net.dean.jraw.models.AccountPreferences;
import net.dean.jraw.models.ThumbnailDisplayPreference;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class helps make a map of data to be sent to the Reddit API to update a user's preferences. Note that this class
 * alone will not make any changes to preferences.
 */
public class AccountPreferencesEditor {
    private final Map<String, Object> args;

    /**
     * Instantiates a new AccountPreferencesEditor
     */
    public AccountPreferencesEditor() {
        this(null);
    }

    /**
     * Instantiates a new AccountPreferencesEditor, whose initial values are set to the original's values
     */
    public AccountPreferencesEditor(AccountPreferences original) {
        this.args = new HashMap<>();

        if (original != null) {
            for (Iterator<Map.Entry<String, JsonNode>> it = original.getDataNode().fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                if (!entry.getValue().isNull())
                    args.put(entry.getKey(), val(entry.getValue()));
            }
        }
    }

    private Object val(JsonNode node) {
        if (node.isNull())
            return null;
        if (node.isBoolean())
            return node.asBoolean();
        if (node.isInt())
            return node.asInt();
        if (node.isTextual())
            return node.asText();
        if (node.isArray() && node.size() == 1)
            // Only one value in the array, retrieve that
            return val(node.get(0));
        throw new IllegalArgumentException("Could not find value for " + node);
    }

    /**
     * Sets the interface language
     * @param s A valid <a href="http://www.inter-locale.com/ID/rfc5646.html">IETF language tag</a>. For example, "en",
     *          "fr", "de"
     */
    public AccountPreferencesEditor lang(String s) {
        args.put("lang", s);
        return this;
    }

    /**
     * Sets whether the user will use the Reddit toolbar to look at links on the website
     */
    public AccountPreferencesEditor redditToolbarEnabled(boolean flag) {
        args.put("frame", flag);
        return this;
    }

    /** Sets whether links will open in a new tab (left click will function as a middle click) */
    public AccountPreferencesEditor newWindow(boolean flag) {
        args.put("newwindow", flag);
        return this;
    }

    /** Sets the user's preference on how thumbnails should be displayed */
    public AccountPreferencesEditor thumbnailDisplayPreference(ThumbnailDisplayPreference pref) {
        args.put("media", pref.name().toLowerCase());
        return this;
    }

    /** Sets whether the user wants the thumbnails to not be displayed next to NSFW links */
    public AccountPreferencesEditor hideNsfwThumbnails(boolean flag) {
        args.put("no_profantiy", flag);
        return this;
    }

    /** Sets if the spotlight box shows on the front page */
    public AccountPreferencesEditor showSpotlightBox(boolean flag) {
        args.put("organic", flag);
        return this;
    }

    /** Sets if trending subreddits will appear on the front page */
    public AccountPreferencesEditor showTrending(boolean flag) {
        args.put("trending", flag);
        return this;
    }

    /** Sets if recently clicked links will be shown */
    public AccountPreferencesEditor showRecentClicks(boolean flag) {
        args.put("clickgadget", flag);
        return this;
    }

    /** Sets if posts will be displayed in a "compressed" fashion */
    public AccountPreferencesEditor compressLink(boolean flag) {
        args.put("compress", flag);
        return this;
    }

    /** Sets if additional information about the domain of a submission will be shown */
    public AccountPreferencesEditor showDomainDetails(boolean flag) {
        args.put("domain_details", flag);
        return this;
    }

    /** Sets if posts (except for your own) will be shown after they have been upvoted */
    public AccountPreferencesEditor hideUpvotedPosts(boolean flag) {
        args.put("hide_ups", flag);
        return this;
    }

    /** Sets if posts (except for your own) will be shown after they have been downvoted */
    public AccountPreferencesEditor hideDownvotedPosts(boolean flag) {
        args.put("hide_downs", flag);
        return this;
    }

    /** Sets the amount of submissions that will be displayed on each page */
    public AccountPreferencesEditor postsPerPage(int val) {
        args.put("numsites", val);
        return this;
    }

    /**
     * Sets the minimum submission score required to be shown to the user.
     * @param val The minimum score a link needs to have to be shown, or null for none.
     */
    public AccountPreferencesEditor linkScoreThreshold(Integer val) {
        if (val == null)
            args.put("min_link_score", "");
        else
            args.put("min_link_score", val);
        return this;
    }

    /**
     * Sets the minimum score a comment needs to have to be not hidden
     * @param val The minimum score, or null for none
     */
    public AccountPreferencesEditor commentScoreThreshold(Integer val) {
        if (val == null)
            args.put("min_comment_score", "");
        else
            args.put("min_comment_score", val);
        return this;
    }

    /** Sets the default amount of comments to load. Up to 500 for normal users, 1,500 for gold users */
    public AccountPreferencesEditor defaultCommentCount(int val) {
        args.put("num_comments", val);
        return this;
    }

    /** Sets if a dagger (†) will be shown next to comments voted controversial */
    public AccountPreferencesEditor highlightControversial(boolean flag) {
        args.put("highlight_controversial", flag);
        return this;
    }

    /** Sets if message conversations will be shown in the inbox */
    public AccountPreferencesEditor showPmThreads(boolean flag) {
        args.put("threaded_messages", flag);
        return this;
    }

    /** Sets if messages will be automatically collapsed after reading them */
    public AccountPreferencesEditor messageAutoCollapse(boolean flag) {
        args.put("collapse_read_messages", flag);
        return this;
    }

    /** Sets if all unread messages will automatically be marked as read once the inbox is opened */
    public AccountPreferencesEditor autoReadMessages(boolean flag) {
        args.put("mark_messages_read", flag);
        return this;
    }

    /** Sets if custom stylesheets will be used if a subreddit has one */
    public AccountPreferencesEditor customStylesheets(boolean flag) {
        args.put("show_stylesheets", flag);
        return this;
    }

    /** Sets if user flair will be shown */
    public AccountPreferencesEditor showUserFlair(boolean flag) {
        args.put("show_flair", flag);
        return this;
    }

    /** Sets if link flair will be shown */
    public AccountPreferencesEditor showLinkFlair(boolean flag) {
        args.put("show_link_flair", flag);
        return this;
    }

    /** Sets if this account's user "over eighteen years old and willing to view adult content" */
    public AccountPreferencesEditor over18(boolean flag) {
        args.put("over_18", flag);
        return this;
    }

    /** Sets if posts will be labeled 'NSFW' if they are not safe for work */
    public AccountPreferencesEditor labelNsfwPosts(boolean flag) {
        args.put("label_nsfw", flag);
        return this;
    }

    /** Sets if private RSS feeds are enabled. */
    public AccountPreferencesEditor privateFeeds(boolean flag) {
        args.put("private_feeds", flag);
        return this;
    }

    /** Sets if your upvotes/downvotes will be public */
    public AccountPreferencesEditor publicVoteHistory(boolean flag) {
        args.put("public_votes", flag);
        return this;
    }

    /** Sets if the user has given consent for their account to be used in research */
    public AccountPreferencesEditor researchable(boolean flag) {
        args.put("research", flag);
        return this;
    }

    /** Sets if the user's profile will be hidden from search engines */
    public AccountPreferencesEditor hideFromSearchEngines(boolean flag) {
        args.put("hide_from_robots", flag);
        return this;
    }

    /** Whether or not to receive email notifications of new messages or not */
    public AccountPreferencesEditor emailMessages(boolean flag) {
        args.put("email_messages", flag);
        return this;
    }

    /** Whether or not to automatically renew reddit gold creddits */
    public AccountPreferencesEditor autorenewCredits(boolean flag) {
        args.put("creddit_autorenew", flag);
        return this;
    }

    /** Whether or not to send username mentions to your inbox */
    public AccountPreferencesEditor monitorMentions(boolean flag) {
        args.put("monitor_mentions", flag);
        return this;
    }

    /** Whether or not to show the user's Snoovatar */
    public AccountPreferencesEditor showSnoovatar(boolean flag) {
        args.put("show_snoovatar", flag);
        return this;
    }

    /** Whether or not to force the use of HTTPS while browsing the website. API clients may choose not to obey this. */
    public AccountPreferencesEditor forceHttps(boolean flag) {
        args.put("force_https", flag);
        return this;
    }

    /** Gets a new copy of the arguments that will be sent to the API */
    public Map<String, Object> getArgs() {
        return new HashMap<>(args);
    }
}
