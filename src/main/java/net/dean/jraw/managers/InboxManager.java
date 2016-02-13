package net.dean.jraw.managers;

import net.dean.jraw.*;
import net.dean.jraw.http.MediaTypes;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RestResponse;
import net.dean.jraw.models.Captcha;
import net.dean.jraw.models.Message;
import net.dean.jraw.util.JrawUtils;

import java.util.Map;

/**
 * This class is responsible for managing a user's inbox
 */
public class InboxManager extends AbstractManager {

    /**
     * Instantiates a new InboxManager
     * @param client The RedditClient to use
     */
    public InboxManager(RedditClient client) {
        super(client);
    }

    /**
     * Sets the message as read or unread. This message must not be a comment (in other words, it must be a private
     * message).
     *
     * @param m The message to mark as read or unread.
     * @param more Any additional messages
     * @param read Whether the message will be marked (true) or unread (false)
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation({
            Endpoints.READ_MESSAGE,
            Endpoints.UNREAD_MESSAGE
    })
    public void setRead(boolean read, Message m, Message... more) throws NetworkException {
        reddit.execute(reddit.request()
                .endpoint(read ? Endpoints.READ_MESSAGE : Endpoints.UNREAD_MESSAGE)
                .post(JrawUtils.mapOf("id", JrawUtils.joinVarargsFullname(m, more)))
                .build());
    }

    /**
     * Sets the message as read or unread. This message must not be a comment (in other words, it must be a private
     * message).
     *
     * @param fullname The fullname of the message teo mark read.
     * @param read Whether the message will be marked (true) or unread (false)
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation({
            Endpoints.READ_MESSAGE,
            Endpoints.UNREAD_MESSAGE
    })
    public void setRead(String fullname, boolean read) throws NetworkException {
        reddit.execute(reddit.request()
                .endpoint(read ? Endpoints.READ_MESSAGE : Endpoints.UNREAD_MESSAGE)
                .post(JrawUtils.mapOf("id", fullname))
                .build());
    }

    /**
     * Sets all unread messages as read.
     * @throws NetworkException If the response code was not 202
     */
    @EndpointImplementation(Endpoints.READ_ALL_MESSAGES)
    public void setAllRead() throws NetworkException {
        RestResponse response = reddit.execute(reddit.request()
                .endpoint(Endpoints.READ_ALL_MESSAGES)
                // Returns the string "202 Accepted\n\nThe request is accepted for processing.      "
                .expected(MediaTypes.PLAIN.type())
                .post()
                .build());
        if (response.getStatusCode() != 202) {
            // Returns 202 if the request was acknowledged
            // See https://www.reddit.com/dev/api/oauth#POST_api_read_all_messages
            throw new IllegalStateException("Expected to return 202 Accepted, got "
                    + response.getStatusCode() + " " + response.getStatusMessage());
        }
    }

    /**
     * Sends a private message as the currently logged in user.
     *
     * @param to Who to send this message to
     * @param subject The message's subject
     * @param body The message's body
     * @throws NetworkException If the request did not complete successfully
     * @throws ApiException If the Reddit API returned an error
     */
    public void compose(String to, String subject, String body) throws NetworkException, ApiException {
        compose("", to, subject, body);
    }

    /**
     * Sends a private message.
     *
     * @param from Who to send this message as. If sending as the currently authenticated user, leave this empty. If
     *             sending as a subreddit you moderate, use the name of the subreddit without "/r/" (for example: "pics")
     * @param to Who to send this message to
     * @param subject The message's subject
     * @param body The message's body
     * @throws NetworkException If the request did not complete successfully
     * @throws ApiException If the Reddit API returned an error
     */
    public void compose(String from, String to, String subject, String body) throws NetworkException, ApiException {
        compose(from, to, subject, body, null, null);
    }

    /**
     * Sends a private message as the currently logged in user with a given captcha.
     * Only really needed if the user has less than 10 link karma.
     *
     * @param to Who to send this message to
     * @param subject The message's subject
     * @param body The message's body
     * @param captcha The Captcha the user is attempting
     * @param captchaAttempt The user's guess at the captcha
     * @throws NetworkException If the request did not complete successfully
     * @throws ApiException If the Reddit API returned an error
     */
    public void compose(String to, String subject, String body, Captcha captcha, String captchaAttempt) throws NetworkException, ApiException {
        compose("", to, subject, body, captcha, captchaAttempt);
    }

    /**
     * Sends a private message.
     *
     * @param from Who to send this message as. If sending as the currently authenticated user, leave this empty. If
     *             sending as a subreddit you moderate, use the name of the subreddit without "/r/" (for example: "pics")
     * @param to Who to send this message to
     * @param subject The message's subject
     * @param body The message's body
     * @param captcha The Captcha the user is attempting
     * @param captchaAttempt The user's guess at the captcha
     * @throws NetworkException If the request did not complete successfully
     * @throws ApiException If the Reddit API returned an error
     */
    @EndpointImplementation(Endpoints.COMPOSE)
    public void compose(String from, String to, String subject, String body, Captcha captcha, String captchaAttempt) throws NetworkException, ApiException {

        Map<String, String> args = JrawUtils.mapOf(
                "api_type", "json",
                "from_sr", from,
                "subject", subject,
                "text", body,
                "to", to
        );

        if (captcha != null) {
            if (captchaAttempt == null) {
                throw new IllegalArgumentException("Captcha present but the attempt is not");
            }

            args.put("iden", captcha.getId());
            args.put("captcha", captchaAttempt);
        }

        RestResponse response = reddit.execute(reddit.request()
                .endpoint(Endpoints.COMPOSE)
                .post(args)
                .build());
        if (response.hasErrors()) {
            throw response.getError();
        }
    }
}
