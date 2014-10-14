package net.dean.jraw.managers;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RestRequest;
import net.dean.jraw.models.Message;
import net.dean.jraw.pagination.InboxPaginator;

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

    @Override
    protected boolean requiresAuthentication(RestRequest r) {
        return true;
    }
}
