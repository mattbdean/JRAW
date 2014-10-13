package net.dean.jraw;

import net.dean.jraw.http.AbstractManager;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.pagination.InboxPaginator;

/**
 * This class is responsible for managing the user's inbox
 */
public class InboxManager extends AbstractManager {
    public InboxManager(LoggedInAccount account) {
        super(account);
    }

    /**
     * Instantiates a new InboxPaginator that can iterate the given user's messages
     * @param where What to iterate
     * @return A new InboxPaginator that iterates over the given location
     */
    public InboxPaginator iterate(InboxPaginator.Where where) {
        return new InboxPaginator(account, where);
    }
}
