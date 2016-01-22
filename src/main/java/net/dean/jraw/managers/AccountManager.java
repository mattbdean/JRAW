package net.dean.jraw.managers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import net.dean.jraw.AccountPreferencesEditor;
import net.dean.jraw.ApiException;
import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.util.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.MediaTypes;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RequestBody;
import net.dean.jraw.http.RestResponse;
import net.dean.jraw.models.AccountPreferences;
import net.dean.jraw.models.Captcha;
import net.dean.jraw.models.Contribution;
import net.dean.jraw.models.FlairTemplate;
import net.dean.jraw.models.KarmaBreakdown;
import net.dean.jraw.models.PublicContribution;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.models.Thing;
import net.dean.jraw.models.UserRecord;
import net.dean.jraw.models.VoteDirection;
import net.dean.jraw.models.attr.Votable;

import java.net.URL;
import java.util.*;

/**
 * This class manages common user actions, such as voting, commenting, saving, etc.
 */
public class AccountManager extends AbstractManager {
    /**
     * Instantiates a new AccountManager
     * @param client The RedditClient to use
     */
    public AccountManager(RedditClient client) {
        super(client);
    }

    /**
     * Submits a new link
     *
     * @param b The SubmissionBuilder to gather data from
     * @return A representation of the newly submitted Submission
     * @throws NetworkException If the request was not successful
     * @throws net.dean.jraw.ApiException If the Reddit API returned an error
     */
    public Submission submit(SubmissionBuilder b) throws NetworkException, ApiException {
        return submit(b, null, null);
    }

    /**
     * Submits a new link with a given captcha. Only really needed if the user has less than 10 link karma.
     *
     * @param b The SubmissionBuilder to gather data from
     * @param captcha The Captcha the user is attempting
     * @param captchaAttempt The user's guess at the captcha
     * @return A representation of the newly submitted Submission
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the API returned an error
     */
    @EndpointImplementation(Endpoints.SUBMIT)
    public Submission submit(SubmissionBuilder b, Captcha captcha, String captchaAttempt) throws NetworkException, ApiException {
        Map<String, String> args = JrawUtils.mapOf(
                "api_type", "json",
                "extension", "json",
                "kind", b.selfPost ? "self" : "link",
                "resubmit", b.resubmit,
                "save", b.saveAfter,
                "sendreplies", b.sendRepliesToInbox,
                "sr", b.subreddit,
                "then", "comments",
                "title", b.title
        );

        if (b.selfPost) {
            args.put("text", b.selfText);
        } else {
            args.put("url", b.url.toExternalForm());
        }

        if (captcha != null) {
            if (captchaAttempt == null) {
                throw new IllegalArgumentException("Captcha present but the attempt is not");
            }

            args.put("iden", captcha.getId());
            args.put("captcha", captchaAttempt);
        }

        RestResponse response = genericPost(reddit.request()
                .endpoint(Endpoints.SUBMIT)
                .post(args)
                .build());
        return reddit.getSubmission(response.getJson().get("json").get("data").get("id").asText());
    }

    /**
     * Votes on a comment or submission. Please note that "API clients proxying a human's action one-for-one are OK, but
     * bots deciding how to vote on content or amplifying a human's vote are not".
     *
     * @param s The submission to vote on
     * @param voteDirection How to vote
     * @param <T> The Votable Thing to vote on
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the API returned an error
     */
    @EndpointImplementation(Endpoints.VOTE)
    public <T extends Thing & Votable> void vote(T s, VoteDirection voteDirection) throws NetworkException, ApiException {
        genericPost(reddit.request()
                .endpoint(Endpoints.VOTE)
                .post(JrawUtils.mapOf(
                                "api_type", "json",
                                "dir", voteDirection.getValue(),
                                "id", s.getFullName())
                ).build());
    }

    /**
     * Saves a given submission
     * @param s The submission to save
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the API returned an error
     */
    public void save(PublicContribution s) throws NetworkException, ApiException {
        setSaved(s, true);
    }

    /**
     * Unsaves a given submission
     * @param s The submission to unsave
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the API returned an error
     */
    public void unsave(PublicContribution s) throws NetworkException, ApiException {
        setSaved(s, false);
    }

    /**
     * Saves or unsaves a submission.
     *
     * @param s The submission to save or unsave
     * @param save Whether or not to save the submission
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the API returned an error
     */
    @EndpointImplementation({Endpoints.SAVE, Endpoints.UNSAVE})
    private void setSaved(PublicContribution s, boolean save) throws NetworkException, ApiException {
        // Send it to "/api/save" if save == true, "/api/unsave" if save == false
        genericPost(reddit.request()
                .endpoint(save ? Endpoints.SAVE : Endpoints.UNSAVE)
                .post(JrawUtils.mapOf(
                        "id", s.getFullName()
                )).build());
    }

    /**
     * Sets whether or not replies to this submission should be sent to your inbox. You must own this Submission.
     *
     * @param s The submission to modify
     * @param send Whether or not to send replies to your inbox
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the API returned an error
     */
    @EndpointImplementation(Endpoints.SENDREPLIES)
    public void sendRepliesToInbox(Submission s, boolean send) throws NetworkException, ApiException {
        genericPost(reddit.request()
                .endpoint(Endpoints.SENDREPLIES)
                .post(JrawUtils.mapOf(
                        "id", s.getFullName(),
                        "state", send
                )).build());
    }

    /**
     * Sets whether or not a submission is hidden
     *
     * @param s The submission to hide or unhide
     * @param hide If the submission is to be hidden
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the API returned an error
     */
    @EndpointImplementation({Endpoints.HIDE, Endpoints.UNHIDE})
    public void hide(boolean hide, Submission s, Submission... more) throws NetworkException, ApiException {
        String ids = s.getFullName();
        if (more != null) {
            String[] idList = new String[more.length];
            for (int i = 0; i < more.length; i++) {
                idList[i] = more[i].getFullName();
            }
            ids = ids + "," + JrawUtils.join(idList);
        }
        genericPost(reddit.request()
                .endpoint(hide ? Endpoints.HIDE : Endpoints.UNHIDE)
                .post(JrawUtils.mapOf(
                        "id", ids
                )).build());
    }

    /**
     * Updates the body of a self-text Submission or Comment
     *
     * @param contribution The self-post or comment that to edit the text for
     * @param text The new body
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the API returned an error
     */
    @EndpointImplementation(Endpoints.EDITUSERTEXT)
    public <T extends PublicContribution> void updateContribution(T contribution, String text) throws NetworkException, ApiException {
        genericPost(reddit.request().endpoint(Endpoints.EDITUSERTEXT)
                .post(JrawUtils.mapOf(
                        "api_type", "json",
                        "text", text,
                        "thing_id", contribution.getFullName()
                )).build());
    }

    /**
     * Sends a reply to a Comment, Submission, or Message.
     *
     * @param contribution The contribution to reply to
     * @param text The body of the message, formatted in Markdown
     * @return The ID of the newly created reply
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the Reddit API returned an error
     */
    @EndpointImplementation(Endpoints.COMMENT)
    public <T extends Contribution> String reply(T contribution, String text) throws NetworkException, ApiException {
        RestResponse response = genericPost(reddit.request()
                .endpoint(Endpoints.COMMENT)
                .post(JrawUtils.mapOf(
                        "api_type", "json",
                        "text", text,
                        "thing_id", contribution.getFullName()
                )).build());

        return response.getJson().get("json").get("data").get("things").get(0).get("data").get("id").asText();
    }

    /**
     * Subscribes to a subreddit
     * @param subreddit The subreddit to subscribe to
     * @throws NetworkException If the request was not successful
     * @see #unsubscribe(Subreddit)
     */
    @EndpointImplementation(Endpoints.SUBSCRIBE)
    public void subscribe(Subreddit subreddit) throws NetworkException {
        setSubscribed(subreddit, true);
    }

    /**
     * Unsubscribes from a subreddit
     * @param subreddit The subreddit to unsubscribe to
     * @throws NetworkException If the request was not successful
     * @see #subscribe(Subreddit)
     */
    public void unsubscribe(Subreddit subreddit) throws NetworkException {
        setSubscribed(subreddit, false);
    }

    /**
     * Subscribe or unsubscribe to a subreddit
     *
     * @param subreddit The subreddit to (un)subscribe to
     * @param sub Whether to subscribe (true) or unsubscribe (false)
     * @throws NetworkException If the request was not successful
     */
    private void setSubscribed(Subreddit subreddit, boolean sub) throws NetworkException {
        reddit.execute(reddit.request()
                .endpoint(Endpoints.SUBSCRIBE)
                .post(JrawUtils.mapOf(
                        "sr", subreddit.getFullName(),
                        "action", sub ? "sub" : "unsub"
                        // JSON is returned on subscribe, HTML is returned on unsubscribe
                )).build());
    }

    /**
     * Gets a list of possible flair templates for this subreddit. See also: {@link #getFlairChoices(Submission)},
     * {@link #getCurrentFlair(String)}, {@link #getCurrentFlair(Submission)}
     *
     * @param subreddit The subreddit to look up
     * @return A list of flair templates
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the Reddit API returned an error
     */
    @EndpointImplementation(Endpoints.FLAIRSELECTOR)
    public List<FlairTemplate> getFlairChoices(String subreddit) throws NetworkException, ApiException {
        ImmutableList.Builder<FlairTemplate> templates = ImmutableList.builder();
        for (JsonNode choiceNode : getFlairChoicesRootNode(subreddit, null).get("choices")) {
            templates.add(new FlairTemplate(choiceNode));
        }

        return templates.build();
    }

    /**
     * Gets a list of possible flair templates for this submission
     * @param link The submission to look up
     * @return A list of flair templates
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the Reddit API returned an error
     */
    public List<FlairTemplate> getFlairChoices(Submission link) throws NetworkException, ApiException {
        ImmutableList.Builder<FlairTemplate> templates = ImmutableList.builder();
        for (JsonNode choiceNode : getFlairChoicesRootNode(link.getSubredditName(), link).get("choices")) {
            templates.add(new FlairTemplate(choiceNode));
        }

        return templates.build();
    }

    /**
     * Gets the current user flair for this subreddit
     * @param subreddit The subreddit to look up
     * @return The flair template that is being used by the authenticated user
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the Reddit API returned an error
     */
    public FlairTemplate getCurrentFlair(String subreddit) throws NetworkException, ApiException {
        return new FlairTemplate(getFlairChoicesRootNode(subreddit, null).get("current"));
    }

    /**
     * Gets the current user flair for this subreddit
     * @param link The submission to look up
     * @return The given submission's current flair
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the Reddit API returned an error
     */
    public FlairTemplate getCurrentFlair(Submission link) throws NetworkException, ApiException {
        return new FlairTemplate(getFlairChoicesRootNode(link.getSubredditName(), link).get("current"));
    }

    /**
     * Enables or disables user flair on a subreddit
     * @param subreddit The subreddit to enable or disable flair on
     * @param enabled If user flair is enabled
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the API returned an error
     */
    @EndpointImplementation(Endpoints.SETFLAIRENABLED)
    public void setFlairEnabled(String subreddit, boolean enabled) throws NetworkException, ApiException {
        RestResponse response = reddit.execute(reddit.request()
                .path("/r/" + subreddit + Endpoints.SETFLAIRENABLED.getEndpoint().getUri())
                .post(JrawUtils.mapOf(
                        "api_type", "json",
                        "enabled", enabled
                ))
                .build());
        if (response.hasErrors()) {
            throw response.getError();
        }
    }

    /** Gives gold to a comment or submission */
    @EndpointImplementation(Endpoints.OAUTH_GOLD_GILD_FULLNAME)
    public void giveGold(PublicContribution target) throws NetworkException, ApiException {
        genericPost(reddit.request()
                .endpoint(Endpoints.OAUTH_GOLD_GILD_FULLNAME, target.getFullName())
                .post()
                .build());
    }

    /** Gives creddits to a user */
    @EndpointImplementation(Endpoints.OAUTH_GOLD_GIVE_USERNAME)
    public void giveGold(String username, int months) throws NetworkException, ApiException {
        genericPost(reddit.request()
                .endpoint(Endpoints.OAUTH_GOLD_GIVE_USERNAME, username)
                .post(JrawUtils.mapOf("months", months))
                .build());
    }

    /**
     * Gets the preferences for this account
     * @param prefs The specifics name of the desired preferences. These can be found
     *              <a href="https://www.reddit.com/dev/api#GET_api_v1_me_prefs">here</a>. Leave empty to fetch all.
     * @return An AccountPreferences that represent this account's preferences
     * @throws NetworkException If the request was not successful
     */
    public AccountPreferences getPreferences(String... prefs) {
        return getPreferences(Arrays.asList(prefs));
    }

    /**
     * Gets the preferences for this account
     * @param prefs The specific names of the desired preferences. These can be found
     *              <a href="https://www.reddit.com/dev/api#GET_api_v1_me_prefs">here</a>.
     * @return An AccountPreferences that represent this account's preferences
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.OAUTH_ME_PREFS_GET)
    public AccountPreferences getPreferences(List<String> prefs) {
        RestResponse response = reddit.execute(reddit.request()
                .endpoint(Endpoints.OAUTH_ME_PREFS_GET)
                .query(JrawUtils.mapOf("fields", JrawUtils.join(prefs)))
                .build());
        return new AccountPreferences(response.getJson());
    }

    /**
     * Updates the preferences for this account
     * @param prefs The preferences
     * @return The preferences after they were updated
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.OAUTH_ME_PREFS_PATCH)
    public AccountPreferences updatePreferences(AccountPreferencesEditor prefs) throws NetworkException {
        RestResponse response = reddit.execute(reddit.request()
                .endpoint(Endpoints.OAUTH_ME_PREFS_PATCH)
                .patch(RequestBody.create(MediaTypes.JSON.type(), JrawUtils.toJson(prefs.getArgs())))
                .build());
        return new AccountPreferences(response.getJson());
    }

    /**
     * Gets a breakdown of link and comment karma by subreddit
     * @return A KarmaBreakdown for this account
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.OAUTH_ME_KARMA)
    public KarmaBreakdown getKarmaBreakdown() throws NetworkException {
        RestResponse response = reddit.execute(reddit.request()
                .endpoint(Endpoints.OAUTH_ME_KARMA)
                .build());
        return new KarmaBreakdown(response.getJson().get("data"));
    }

    /**
     * Removes a friend
     * @param friend The username of the friend
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.OAUTH_ME_FRIENDS_USERNAME_DELETE)
    public void deleteFriend(String friend) throws NetworkException {
        reddit.execute(reddit.request()
                .delete()
                .endpoint(Endpoints.OAUTH_ME_FRIENDS_USERNAME_DELETE, friend)
                .build());
    }

    /**
     * Gets a user record pertaining to a particular relationship
     * @param name The name of the user
     * @return A UserRecord representing the relationship
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.OAUTH_ME_FRIENDS_USERNAME_GET)
    public UserRecord getFriend(String name) throws NetworkException {
        RestResponse response = reddit.execute(reddit.request()
                .endpoint(Endpoints.OAUTH_ME_FRIENDS_USERNAME_GET, name)
                .build());
        return new UserRecord(response.getJson());
    }

    /**
     * Adds of updates a friend
     * @param name The name of the user
     * @throws NetworkException If the request was not successful
     * @return A UserRecord representing the new or updated relationship
     */
    @EndpointImplementation(Endpoints.OAUTH_ME_FRIENDS_USERNAME_PUT)
    public UserRecord updateFriend(String name) throws NetworkException {
        RestResponse response = reddit.execute(reddit.request()
                .put(RequestBody.create(MediaTypes.JSON.type(), JrawUtils.toJson(new FriendModel(name))))
                .endpoint(Endpoints.OAUTH_ME_FRIENDS_USERNAME_PUT, name)
                .build());
        return new UserRecord(response.getJson());
    }

    private static final class FriendModel {
        private final String name;

        private FriendModel(String name) {
            this.name = name == null ? "" : name;
        }

        public String getName() {
            return name;
        }
    }

    private JsonNode getFlairChoicesRootNode(String subreddit, Submission link) throws NetworkException, ApiException {
        String linkFullname = link != null ? link.getFullName() : null;
        Map<String, String> formArgs = new HashMap<>();
        if (linkFullname != null) {
            formArgs.put("link", linkFullname);
        }

        RestResponse response = genericPost(reddit.request()
                .path("/r/" + subreddit + Endpoints.FLAIRSELECTOR.getEndpoint().getUri())
                .post(formArgs.isEmpty() ? null : formArgs)
                .build());
        return response.getJson();
    }

    /**
     * This class provides a way to configure posting parameters of a new submission
     */
    public static class SubmissionBuilder {
        private final boolean selfPost;
        private final String selfText;
        private final URL url;
        private final String subreddit;
        private final String title;
        private boolean saveAfter; // = false;
        private boolean sendRepliesToInbox; // = false;
        private boolean resubmit = true;

        /**
         * Instantiates a new SubmissionBuilder that will result in a self post.
         * @param selfText The body text of the submission, formatted in Markdown
         * @param subreddit The subreddit to submit the link to (e.g. "funny", "pics", etc.)
         * @param title The title of the submission
         */
        public SubmissionBuilder(String selfText, String subreddit, String title) {
            this.selfPost = true;
            this.selfText = selfText;
            this.url = null;
            this.subreddit = subreddit;
            this.title = title;
        }

        /**
         * Instantiates a new SubmissionBuilder that will result in a link post.
         * @param url The URL that this submission will link to
         * @param subreddit The subreddit to submit the link to (e.g. "funny", "pics", etc.)
         * @param title The title of the submission
         */
        public SubmissionBuilder(URL url, String subreddit, String title) {
            this.selfPost = false;
            this.url = url;
            this.selfText = null;
            this.subreddit = subreddit;
            this.title = title;
        }

        /**
         * Whether to save after right after posting
         * @param flag To save or not to save, that is the question
         * @return This builder
         */
        public SubmissionBuilder saveAfter(boolean flag) {
            this.saveAfter = flag;
            return this;
        }

        /**
         * Whether to send top-level replies to your inbox
         * @param flag Send replies to your inbox?
         * @return This builder
         */
        public SubmissionBuilder sendRepliesToInbox(boolean flag) {
            this.sendRepliesToInbox = flag;
            return this;
        }

        /**
         * Set whether or not the Reddit API will return an error if the link's URL has already been posted
         * @param flag If there should be an exception if there is already a post like this
         * @return This builder
         */
        public SubmissionBuilder resubmit(boolean flag) {
            this.resubmit = flag;
            return this;
        }
    }
}
