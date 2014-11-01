package net.dean.jraw.managers;

import net.dean.jraw.ApiException;
import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RedditResponse;
import net.dean.jraw.models.Message;
import net.dean.jraw.paginators.InboxPaginator;

/**
 * This class is responsible for managing the user's inbox
 */
public class InboxManager extends AbstractManager {

    public InboxManager(RedditClient client) {
        super(client);
    }

    /**
     * Instantiates a new InboxPaginator that can iterate the given user's messages
     * @param where What to iterate
     * @return A new InboxPaginator that iterates over the given location
     */
    public InboxPaginator iterate(InboxPaginator.Where where) {
        return new InboxPaginator(reddit, where);
    }

    /**
     * Sets the message as read or unread. This message must not be a comment (in other words, it must be a private
     * message).
     *
     * @param m The message to mark as read or unread.
     * @param read Whether the message will be marked (true) or unread (false)
     * @throws NetworkException
     */
    @EndpointImplementation({
            Endpoints.READ_MESSAGE,
            Endpoints.UNREAD_MESSAGE
    })
    public void setRead(Message m, boolean read) throws NetworkException {
        execute(request()
                .endpoint(read ? Endpoints.READ_MESSAGE : Endpoints.UNREAD_MESSAGE)
                .post(JrawUtils.args("id", m.getFullName()))
                .build());
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
    @EndpointImplementation(Endpoints.COMPOSE)
    public void compose(String from, String to, String subject, String body) throws NetworkException, ApiException {
        RedditResponse response = execute(request()
                .endpoint(Endpoints.COMPOSE)
                .post(JrawUtils.args(
                        "api_type", "json",
                        "from_sr", from,
                        "subject", subject,
                        "text", body,
                        "to", to
                )).build());
        if (response.hasErrors()) {
            throw response.getErrors()[0];
        }
        System.out.println();
    }
}
