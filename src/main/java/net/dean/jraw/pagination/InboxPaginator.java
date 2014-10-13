package net.dean.jraw.pagination;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.Message;

public class InboxPaginator extends GenericPaginator<Message, InboxPaginator.Where> {
    /**
     * Instantiates a new InboxPaginator
     *
     * @param account The account to retrieve messages from
     * @param where   The "where" enum value to use
     */
    public InboxPaginator(LoggedInAccount account, Where where) {
        super(account.getCreator(), Message.class, where);
    }

    @Override
    public String getUriPrefix() {
        return "/message";
    }

    @Override
    public String getAsString(Where where) {
        return where.name().toLowerCase().replace('_', '/');
    }

    @Override
    @EndpointImplementation({
            Endpoints.MESSAGE_INBOX,
            Endpoints.MESSAGE_SENT,
            Endpoints.MESSAGE_UNREAD,
            Endpoints.MESSAGE_WHERE
    })
    protected Listing<Message> getListing(boolean forwards) throws NetworkException {
        // Just call super so that we can add the @EndpointImplementation annotation
        return super.getListing(forwards);
    }

    public enum Where {
        INBOX,
        UNREAD,
        MESSAGES,
        SENT,
        MODERATOR,
        MODERATOR_UNREAD
    }
}
