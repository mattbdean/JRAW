package net.dean.jraw.fluent;

import net.dean.jraw.ApiException;
import net.dean.jraw.models.Message;
import net.dean.jraw.paginators.InboxPaginator;

/**
 * A reference to an authenticated-user's inbox
 */
public final class InboxReference extends ElevatedAbstractReference {

    /**
     * Instantiates a new InboxReference
     *
     * @param managers A manager aggregation. Must not be null.
     */
    protected InboxReference(ManagerAggregation managers) {
        super(managers);
    }

    /**
     * Creates a new Paginator that will iterate through unread messages. Equivalent to
     * {@code read("unread")}.
     */
    public InboxPaginator read() {
        return read("unread");
    }

    /**
     * Creates a new Paginator that will iterate through the inbox.
     * @param what One of "inbox", "unread", "messages", "sent", "moderator", or "moderator/unread"
     */
    public InboxPaginator read(String what) {
        return new InboxPaginator(reddit, what);
    }

    /**
     * Composes a message
     * @throws ApiException If the reddit API returned an error
     */
    @NetworkingCall
    public void compose(String to, String subject, String body) throws ApiException {
        managers.inbox().compose(to, subject, body);
    }

    /** Mark a given message as 'read' */
    @NetworkingCall
    public void readMessage(boolean read, Message m, Message... more) {
        managers.inbox().setRead(read, m, more);
    }

    /** Mark a given message as 'read' */
    @NetworkingCall
    public void readMessage(String fullname, boolean read) {
        managers.inbox().setRead(fullname, read);
    }

    /** Mark all unread messages as 'read' */
    @NetworkingCall
    public void readAllMessages() {
        managers.inbox().setAllRead();
    }
}
