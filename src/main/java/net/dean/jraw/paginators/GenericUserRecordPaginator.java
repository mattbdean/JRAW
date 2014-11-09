package net.dean.jraw.paginators;

import com.google.common.collect.ImmutableList;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.RedditResponse;
import net.dean.jraw.models.FauxListing;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.UserRecord;
import org.codehaus.jackson.JsonNode;

/**
 * This class provides the basic framework required to create a Paginator that returns listings of UserRecords
 * @param <T> The generic "where" value to use
 */
public abstract class GenericUserRecordPaginator<T extends Enum<T>> extends GenericPaginator<UserRecord, T> {
    /**
     * Instantiates a new GenericUserRecordPaginator
     *
     * @param creator The RedditClient that will be used to send requests
     * @param where   The "where" enum value to use
     */
    protected GenericUserRecordPaginator(RedditClient creator, T where) {
        super(creator, UserRecord.class, where);
    }

    @Override
    protected Listing<UserRecord> parseListing(RedditResponse response) {
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

        return new FauxListing<>(list.build(), getJsonValue(data, "before"),
                getJsonValue(data, "after"), getJsonValue(data, "after"));
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
