package net.dean.jraw.paginators;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.UserRecord;

/**
 * This class provides the ability to paginate over a user's friends and blocked users
 */
public class ImportantUserPaginator extends GenericUserRecordPaginator<ImportantUserPaginator.Where> {
    /**
     * Instantiates a new ImportantUserPaginator
     *
     * @param creator    The RedditClient that will be used to send requests
     * @param where      The "where" enum value to use
     */
    public ImportantUserPaginator(RedditClient creator, Where where) {
        super(creator, UserRecord.class, where);
    }

    @Override
    @EndpointImplementation({
            Endpoints.PREFS_BLOCKED,
            Endpoints.PREFS_FRIENDS,
            Endpoints.OAUTH_ME_BLOCKED,
            Endpoints.OAUTH_ME_FRIENDS
    })
    protected Listing<UserRecord> getListing(boolean forwards) throws NetworkException, IllegalStateException {
        return super.getListing(forwards);
    }

    @Override
    protected String getUriPrefix() {
        return "/prefs/";
    }

    public static enum Where {
        FRIENDS,
        BLOCKED
    }
}
