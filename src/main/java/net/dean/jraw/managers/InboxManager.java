package net.dean.jraw.managers;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
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
}
