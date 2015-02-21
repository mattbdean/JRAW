package net.dean.jraw.paginators;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.UserRecord;

/**
 * This class provides the ability to paginate over a user's friends and blocked users
 */
public class ImportantUserPaginator extends GenericUserRecordPaginator {
    /**
     * Instantiates a new ImportantUserPaginator
     *
     * @param creator The RedditClient that will be used to send requests
     * @param where   What to look up
     */
    public ImportantUserPaginator(RedditClient creator, String where) {
        super(creator, where);
    }

    @Override
    @EndpointImplementation({
            Endpoints.PREFS_WHERE,
            Endpoints.PREFS_BLOCKED,
            Endpoints.PREFS_FRIENDS,
            Endpoints.OAUTH_ME_BLOCKED,
            Endpoints.OAUTH_ME_FRIENDS
    })
    public Listing<UserRecord> next(boolean forceNetwork) throws IllegalStateException {
        return super.next(forceNetwork);
    }

    @Override
    protected String getUriPrefix() {
        return "/prefs/";
    }

    @Override
    public String[] getWhereValues() {
        return new String[] {"friends", "blocked"};
    }
}
