package net.dean.jraw.models;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import net.dean.jraw.*;
import net.dean.jraw.http.NetworkAccessible;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RedditResponse;
import net.dean.jraw.models.core.Account;
import net.dean.jraw.models.core.Comment;
import net.dean.jraw.models.core.Submission;
import net.dean.jraw.models.core.Thing;
import org.codehaus.jackson.JsonNode;

import java.net.URL;
import java.util.Map;

public class LoggedInAccount extends Account implements NetworkAccessible<RedditClient> {
    private RedditClient creator;

    public LoggedInAccount(JsonNode data, RedditClient creator) {
        super(data);
        this.creator = creator;
    }

    /**
     * Submits a new link
     *
     * @param b The SubmissionBuilder to gather data from
     * @return A representation of the newly submitted Submission
     * @throws NetworkException If there was a problem sending the HTTP request
     * @throws ApiException If the Reddit API returned an error
     */
    public Submission submitContent(SubmissionBuilder b) throws NetworkException, ApiException {
        return submitContent(b, null, null);
    }

    /**
     * Submits a new link with a given captcha. Only really needed if the user has less than 10 link karma.
     *
     * @param b The SubmissionBuilder to gather data from
     * @param captcha The Captcha the user is attempting
     * @param captchaAttempt The user's guess at the captcha
     * @return A representation of the newly submitted Submission
     * @throws NetworkException If there was a problem sending the HTTP request
     * @throws net.dean.jraw.ApiException If the API returned an error
     */
    @EndpointImplementation(Endpoints.SUBMIT)
    public Submission submitContent(SubmissionBuilder b, Captcha captcha, String captchaAttempt) throws NetworkException, ApiException {
        Map<String, String> args = JrawUtils.args(
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
            args.put("url", b.url.toExternalForm());
        } else {
            args.put("text", b.selfText);
        }

        if (captcha != null) {
            if (captchaAttempt == null) {
                throw new IllegalArgumentException("Captcha present but the attempt is not");
            }

            args.put("iden", captcha.getId());
            args.put("captcha", captchaAttempt);
        }

        FormEncodingBuilder form = new FormEncodingBuilder();
        for (Map.Entry<String, String> entry : args.entrySet()) {
            form.add(entry.getKey(), entry.getValue());
        }

        RedditResponse response = genericPost(creator.request()
                .endpoint(Endpoints.SUBMIT)
                .post(form.build())
                .build());
        return creator.getSubmission(response.getJson().get("json").get("data").get("id").asText());
    }

    /**
     * Votes on a comment or submission. Please note that "API clients proxying a human's action one-for-one are OK, but
     * bots deciding how to vote on content or amplifying a human's vote are not".
     *
     * @param s The submission to vote on
     * @param voteDirection How to vote
     * @param <T> The Votable Thing to vote on
     * @throws NetworkException If there was a problem sending the HTTP request
     * @throws ApiException If the API returned an error
     */
    @EndpointImplementation(Endpoints.VOTE)
    public <T extends Thing & Votable> void vote(T s, VoteDirection voteDirection) throws NetworkException, ApiException {
        genericPost(creator.request()
                .endpoint(Endpoints.VOTE)
                .post(new FormEncodingBuilder()
                        .add("api_type", "json")
                        .add("dir", Integer.toString(voteDirection.getValue()))
                        .add("id", s.getFullName())
                        .build())
                .build());
    }

    /**
     * Saves or unsaves a submission.
     *
     * @param s The submission to save or unsave
     * @param save Whether or not to save the submission
     * @return The JSON response from the API
     * @throws NetworkException If there was a problem sending the HTTP request
     * @throws ApiException If the API returned an error
     */
    @EndpointImplementation({Endpoints.SAVE, Endpoints.UNSAVE})
    public RedditResponse save(Submission s, boolean save) throws NetworkException, ApiException {
        // Send it to "/api/save" if save == true, "/api/unsave" if save == false
        return genericPost(creator.request()
                .endpoint(save ? Endpoints.SAVE : Endpoints.UNSAVE)
                .post(new FormEncodingBuilder()
                        .add("id", s.getFullName())
                        .build())
                .build());
    }

    /**
     * Sets whether or not replies to this submission should be sent to your inbox. You must own this Submission.
     *
     * @param s The submission to modify
     * @param send Whether or not to send replies to your inbox
     * @return The response returned from the Reddit API
     * @throws NetworkException If there was a problem sending the HTTP request
     * @throws ApiException If the API returned an error
     */
    @EndpointImplementation(Endpoints.SENDREPLIES)
    public RedditResponse setSendRepliesToInbox(Submission s, boolean send) throws NetworkException, ApiException {
        if (!s.getAuthor().equals(getFullName())) {
            throw new IllegalArgumentException(String.format("Logged in user (%s) did not post this submission (by %s)", getFullName(), s.getAuthor()));
        }
        return genericPost(creator.request()
                .endpoint(Endpoints.SENDREPLIES)
                .post(new FormEncodingBuilder()
                        .add("id", s.getFullName())
                        .add("state", Boolean.toString(send))
                        .build())
                .build());
    }

    /**
     * Sets whether or not this submission should be marked as not safe for work
     *
     * @param s The submission to modify
     * @param nsfw Whether or not this submission is not safe for work
     * @return The response returned from the Reddit API
     * @throws NetworkException If there was a problem sending the HTTP request
     * @throws ApiException If the API returned an error
     */
    @EndpointImplementation({Endpoints.MARKNSFW, Endpoints.UNMARKNSFW})
    public RedditResponse setNsfw(Submission s, boolean nsfw) throws NetworkException, ApiException {
        checkIfOwns(s);

        // "/api/marknsfw" if nsfw == true, "/api/unmarknsfw" if nsfw == false
        return genericPost(creator.request()
                .endpoint(nsfw ? Endpoints.MARKNSFW : Endpoints.UNMARKNSFW)
                .post(new FormEncodingBuilder()
                        .add("id", s.getFullName())
                        .build())
                .build());
    }

    /**
     * Deletes a submission that you posted
     * @param thing The submission to delete
     * @param <T> The Votable Thing to delete
     * @return The response that the Reddit API returned
     * @throws NetworkException If there was a problem sending the request
     * @throws ApiException If the API returned an error
     */
    public <T extends Thing & Votable> RedditResponse delete(T thing) throws NetworkException, ApiException {
        if (thing instanceof Submission) {
            checkIfOwns((Submission) thing);
        } else if (thing instanceof Comment) {
            Comment c = (Comment) thing;
            if (!c.getAuthor().equals(getFullName())) {
                throw new IllegalArgumentException(String.format("Logged in user (%s) did not post this comment (by %s)", getFullName(), c.getAuthor()));
            }
        } else {
            throw new IllegalArgumentException("Unknown Votable: " + thing.getClass());
        }

        return delete(thing.getFullName());
    }


    /**
     * Deletes a comment or submission that you posted
     * @param id The ID of the submission or comment to delete
     * @return The response that the Reddit API returned
     * @throws NetworkException If there was a problem sending the request
     * @throws ApiException If the API returned an error
     */
    @EndpointImplementation(Endpoints.DEL)
    public RedditResponse delete(String id) throws NetworkException, ApiException {
        return genericPost(creator.request()
                .endpoint(Endpoints.DEL)
                .post(new FormEncodingBuilder()
                        .add("id", id)
                        .build())
                .build());
    }

    /**
     * Adds a user as a developer of an application. See <a href="https://ssl.reddit.com/prefs/apps/">here</a> for more.
     *
     * @param clientId Your application's client ID. You must be a developer of the app.
     * @param newDev The username of the new developer
     * @return The response that the Reddit API returned
     * @throws NetworkException If there was a problem sending the request
     * @throws ApiException If the API returned an error
     */
    public RedditResponse addDeveloper(String clientId, String newDev) throws NetworkException, ApiException {
        return modifyDeveloperStatus(clientId, newDev, false);
    }

    /**
     * Removes a user as a developer of an application. See <a href="https://ssl.reddit.com/prefs/apps/">here</a> for more.
     *
     * @param clientId Your application's client ID. You must be a developer of the app.
     * @param oldDev The username of the (soon-to-be) former developer
     * @return The response that the Reddit API returned
     * @throws NetworkException If there was a problem sending the request
     * @throws ApiException If the api returned an error
     */
    public RedditResponse removeDeveloper(String clientId, String oldDev) throws NetworkException, ApiException {
        return modifyDeveloperStatus(clientId, oldDev, true);
    }

    @EndpointImplementation({Endpoints.ADDDEVELOPER, Endpoints.REMOVEDEVELOPER})
    private RedditResponse modifyDeveloperStatus(String clientId, String devName, boolean remove) throws NetworkException, ApiException {
        return genericPost(creator.request()
                .endpoint(remove ? Endpoints.REMOVEDEVELOPER : Endpoints.ADDDEVELOPER)
                .post(new FormEncodingBuilder()
                        .add("api_type", "json")
                        .add("client_id", clientId)
                        .add("name", devName)
                        .build())
                .build());
    }

    /**
     * Sets whether or not a submission is hidd
     *
     * @param s The submission to hide or unhide
     * @param hide If the submission is to be hidden
     * @return The response that the Reddit API returned
     * @throws NetworkException If there was a problem sending the request
     * @throws ApiException If the API returned an error
     */
    @EndpointImplementation({Endpoints.HIDE, Endpoints.UNHIDE})
    public RedditResponse hide(Submission s, boolean hide) throws NetworkException, ApiException {
        return genericPost(creator.request()
                .endpoint(hide ? Endpoints.HIDE : Endpoints.UNHIDE)
                .post(new FormEncodingBuilder()
                        .add("id", s.getFullName())
                        .build())
                .build());
    }

    /**
     * Executes a generic POST request that returns a RedditResponse. Used primarily for convenience and standardization
     * of the messages of RedditExceptions that are thrown.
     *
     * @param r The request to execute
     * @return A representation of the response by the Reddit API
     * @throws NetworkException If needsLogin is true and the user was not logged in, or there was an error making the
     *                          HTTP request.
     */
    private RedditResponse genericPost(Request r) throws NetworkException, ApiException {
        if (!r.method().equals("POST")) {
            throw new IllegalArgumentException("Request is not POST");
        }

        RedditResponse response = creator.execute(r);
        if (response.hasErrors()) {
            throw response.getApiExceptions()[0];
        }

        return response;
    }

    /**
     * Sends a reply to a Submission.
     *
     * @param parent The submission to reply to
     * @param text The body of the message, formatted in Markdown
     * @return The full name of the newly created reply
     * @throws NetworkException If there was an error making the HTTP request
     * @throws ApiException If the Reddit API returned an error
     */
    public String reply(Submission parent, String text) throws NetworkException, ApiException {
        return reply(parent.getFullName(), text);
    }

    /**
     * Sends a reply to a Comment.
     *
     * @param parent The comment to reply to
     * @param text The body of the message, formatted in Markdown
     * @return The ID of the newly created reply
     * @throws NetworkException If there was an error making the HTTP request
     * @throws ApiException If the Reddit API returned an error
     */
    public String reply(Comment parent, String text) throws NetworkException, ApiException {
        return reply(parent.getFullName(), text);
    }

    /**
     * Sends a reply to a Comment, Submission, or Message.
     *
     * @param name The fullname of the comment, submission, or message
     * @param text The body of the message, formatted in Markdown
     * @return The ID of the newly created reply
     * @throws NetworkException If there was an error making the HTTP request
     * @throws ApiException If the Reddit API returned an error
     */
    @EndpointImplementation(Endpoints.COMMENT)
    private String reply(String name, String text) throws NetworkException, ApiException {
        RedditResponse response = genericPost(creator.request()
                .endpoint(Endpoints.COMMENT)
                .post(new FormEncodingBuilder()
                        .add("api_type", "json")
                        .add("text", text)
                        .add("thing_id", name)
                        .build())
                .build());

        return response.getJson().get("json").get("data").get("things").get(0).get("data").get("id").asText();
    }

    private void checkIfOwns(Submission s) {
        if (!s.getAuthor().equals(getFullName())) {
            throw new IllegalArgumentException(String.format("Logged in user (%s) did not post this submission (by %s)", getFullName(), s.getAuthor()));
        }
    }

    /**
     * Gets the RedditClient that created this LoggedInAccount
     * @return The creator of this object
     */
    @Override
    public RedditClient getCreator() {
        return creator;
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
        private boolean resubmit; // = false;

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
            this.selfPost = true;
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
