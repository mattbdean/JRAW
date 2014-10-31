package net.dean.jraw.paginators;

import com.google.common.collect.ImmutableList;
import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RedditResponse;
import net.dean.jraw.models.FauxListing;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.UserRecord;
import org.codehaus.jackson.JsonNode;

public class UserRecordPaginator extends GenericPaginator<UserRecord, UserRecordPaginator.Where> {
    private String subreddit;

    /**
     * Instantiates a new GenericPaginator
     *
     * @param creator The RedditClient that will be used to send requests
     * @param subreddit The subreddit to view the user records from. The logged in user must be a moderator of this
     *                  subreddit.
     * @param where What to iterate
     */
    public UserRecordPaginator(RedditClient creator, String subreddit, Where where) {
        super(creator, UserRecord.class, where);
        this.subreddit = subreddit;
    }

    @Override
    @EndpointImplementation({
            Endpoints.ABOUT_BANNED,
            Endpoints.ABOUT_WIKIBANNED,
            Endpoints.ABOUT_CONTRIBUTORS,
            Endpoints.ABOUT_WIKICONTRIBUTORS,
            Endpoints.ABOUT_MODERATORS,
            Endpoints.ABOUT_WHERE
    })
    protected Listing<UserRecord> getListing(boolean forwards) throws NetworkException {
        // Just call super so that we can add the @EndpointImplementation annotation
        return super.getListing(forwards);
    }

    @Override
    protected Listing<UserRecord> parseListing(RedditResponse response) {
        ImmutableList.Builder<UserRecord> list = ImmutableList.builder();

        for (JsonNode child : response.getJson().get("data").get("children")) {
            list.add(new UserRecord(child));
        }

        JsonNode data = response.getJson().get("data");
        return new FauxListing<>(list.build(), getJsonValue(data, "before"),
                getJsonValue(data, "after"), getJsonValue(data, "after"));
    }

    private String getJsonValue(JsonNode data, String key) {
        if (where == Where.MODERATORS) {
            // A moderator listing only has a 'children' key
            return null;
        }

        JsonNode node = data.get(key);
        if (node.isNull()) {
            return null;
        }
        return node.asText();
    }

    @Override
    protected String getUriPrefix() {
        return "/r/" + subreddit + "/about/";
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
        invalidate();
    }

    public enum Where {
        BANNED,
        WIKIBANNED,
        CONTRIBUTORS,
        WIKICONTRIBUTORS,
        MODERATORS
    }
}
