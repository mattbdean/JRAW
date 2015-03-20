package net.dean.jraw.paginators;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.RestResponse;
import net.dean.jraw.models.FauxListing;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.UserRecord;

/**
 * This class provides a bridge between GenericPaginator and concrete subclasses of {@code GenericPaginator<UserRecord>}
 * by providing an implementation of {@link #parseListing(RestResponse)} suitable for creating
 * {@code Listing<UserRecord>} objects.
 */
public abstract class GenericUserRecordPaginator extends GenericPaginator<UserRecord> {
    /**
     * Instantiates a new GenericUserRecordPaginator
     *
     * @param creator The RedditClient that will be used to send requests
     * @param where   The "where" enum value to use
     */
    protected GenericUserRecordPaginator(RedditClient creator, String where) {
        super(creator, UserRecord.class, where);
    }

    @Override
    protected Listing<UserRecord> parseListing(RestResponse response) {
        ImmutableList.Builder<UserRecord> list = ImmutableList.builder();

        JsonNode data;
        if (response.getJson().isArray()) {
            // Sometimes the listing is wrapped in an array node for compatability reasons. See
            // https://github.com/reddit/reddit/blob/7d1f80c/r2/r2/controllers/listingcontroller.py#L1511-L1517
            // for a full explanation.
            data = response.getJson().get(0).get("data");
        } else {
            data = response.getJson().get("data");
        }
        for (JsonNode child : data.get("children")) {
            list.add(new UserRecord(child));
        }

        return new FauxListing<>(list.build(),
                getJsonValue(data, "before"),
                getJsonValue(data, "after"));
    }

    private String getJsonValue(JsonNode data, String key) {
        if (!data.has(key)) {
            return null;
        }

        JsonNode node = data.get(key);
        if (node.isNull()) {
            return null;
        }
        return node.asText();
    }
}
