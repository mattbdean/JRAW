package net.dean.jraw.paginators;

import com.google.common.collect.ImmutableList;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.RestResponse;
import net.dean.jraw.models.FauxListing;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Thing;
import net.dean.jraw.models.meta.JsonProperty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is specially designed to iterate <a href="https://www.reddit.com/r/multihub">/r/multihub</a> and pick out
 * information about multireddits submitted there.
 */
public class MultiHubPaginator extends Paginator<MultiHubPaginator.MultiRedditId> {

    /**
     * Matches a multireddit URL. See <a href="http://regexr.com/39k3v">here</a> for a demonstration, and
     * <a href="http://pastebin.com/raw.php?i=0dFYCMRb">here</a> for a list of URLs to test on<br>
     * <br>
     * Usernames must only contain alphanumeric characters, underscores, and hyphens with a max length of 20
     * (<a href="https://github.com/reddit/reddit/blob/c86113850/r2/r2/lib/validator/validator.py#L1311">source</a>).<br>
     * <br>
     * Multireddits must only contain alphanumeric characters, underscores, and hyphens, and have a max length of 21
     * (<a href="https://github.com/reddit/reddit/blob/3b7b74148/r2/r2/lib/validator/validator.py#L2622">source</a>)
     */
    public static final String MULTIREDDIT_URL_REGEX =
            "http(s)?://(\\w.*\\.)?reddit\\.com/user/([a-zA-Z\\-_]*?)/m/([A-Za-z0-9][A-Za-z0-9_]{1,20})";
    private static final int BACKREF_USERNAME = 3;
    private static final int BACKREF_MULTINAME = 4;
    private final Matcher matcher;

    /**
     * Instantiates a new MultiHubPaginator
     * @param client The RedditClient to use
     */
    public MultiHubPaginator(RedditClient client) {
        super(client, MultiRedditId.class);
        // Blank matcher for now, reset(String) will be called later
        this.matcher = Pattern.compile(MULTIREDDIT_URL_REGEX).matcher("");
    }

    @Override
    protected String getBaseUri() {
        return String.format("/r/multihub/%s", sorting.name().toLowerCase());
    }

    @Override
    protected Listing<MultiRedditId> parseListing(RestResponse response) {
        ImmutableList.Builder<MultiRedditId> multiReddits = ImmutableList.builder();

        Listing<Submission> submissions = response.asListing(Submission.class);

        // Iterate over the submissions
        for (Submission potentialMultiLink : submissions) {
            matcher.reset(potentialMultiLink.getUrl());
            if (matcher.matches()) {
                // Found a link to a multireddit, parse the info from the regex
                String owner = matcher.group(BACKREF_USERNAME);
                String multiName = matcher.group(BACKREF_MULTINAME);
                MultiRedditId id = new MultiRedditId(owner, multiName);

                multiReddits.add(id);
            }
        }

        return new FauxListing<>(multiReddits.build(),
                submissions.getBefore(),
                submissions.getAfter(),
                submissions.getMoreChildren());
    }

    /**
     * This class is a "hacky" way to return a MultiReddit's name and owner without actually having to send any HTTP
     * requests. Any inherited methods annotated with {@link JsonProperty} will throw a NullPointerException.
     */
    public static final class MultiRedditId extends Thing {
        private final String owner;
        private final String name;

        /**
         * Instantiates a new MutliRedditId
         * @param owner The owner of the multireddit
         * @param name The name of the multireddit
         */
        public MultiRedditId(String owner, String name) {
            super(null);
            this.owner = owner;
            this.name = name;
        }

        public String getOwner() {
            return owner;
        }

        public String getName() {
            return name;
        }
    }
}
