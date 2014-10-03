package net.dean.jraw.pagination;

import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.RedditResponse;
import net.dean.jraw.models.MultiReddit;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Submission;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is specially designed to iterate <a href="https://www.reddit.com/r/multihub">/r/multihub</a> and pick out information
 * about multireddits submitted there.
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
    private final ObjectMapper objectMapper;
    private final Matcher matcher;

    public MultiHubPaginator(RedditClient client) {
        super(client, MultiRedditId.class);
        this.objectMapper = new ObjectMapper();
        // Blank matcher for now, reset(String) will be called later
        this.matcher = Pattern.compile(MULTIREDDIT_URL_REGEX).matcher("");
    }

    @Override
    protected String getBaseUri() {
        return String.format("/r/multihub/%s.json", sorting.name().toLowerCase());
    }

    @Override
    protected Listing<MultiRedditId> parseListing(RedditResponse response) {
        List<MultiRedditId> multiReddits = new ArrayList<>();

        Listing<Submission> submissions = response.asListing(Submission.class);

        // Iterate over the submissions
        for (Submission potentialMultiLink : submissions) {
            matcher.reset(potentialMultiLink.getUrl().toExternalForm());
            if (matcher.matches()) {
                // Found a link to a multireddit, parse the info from the regex
                String owner = matcher.group(BACKREF_USERNAME);
                String multiName = matcher.group(BACKREF_MULTINAME);
                MultiRedditId id = new MultiRedditId(owner, multiName);

                multiReddits.add(id);
            }
        }

        try (StringWriter writer = new StringWriter()) {
            JsonGenerator gen = new JsonFactory().createJsonGenerator(writer);

            // Start JSON
            gen.writeStartObject();

            // Write children
            gen.writeArrayFieldStart("children");
            for (MultiRedditId id : multiReddits) {
                // {"name": <name>, "owner": <owner>}
                gen.writeStartObject();
                gen.writeStringField("name", id.getName());
                gen.writeStringField("owner", id.getOwner());
                gen.writeStringField("kind", getClass().getSimpleName());
                gen.writeEndObject();
            }
            gen.writeEndArray();

            // Write before, after, and modhash
            writeNullableStringField(gen, "before", submissions.getBefore());
            writeNullableStringField(gen, "after", submissions.getAfter());
            writeNullableStringField(gen, "modhash", submissions.getModhash());

            // End JSON
            gen.writeEndObject();
            gen.close();

            JsonNode node = objectMapper.readTree(writer.toString());
            return new Listing<>(node, MultiRedditId.class);
        } catch (IOException e) {
            JrawUtils.logger().error("Could not create JsonNode", e);
            return null;
        }
    }

    private void writeNullableStringField(JsonGenerator gen, String name, String str) throws IOException {
        if (str == null) {
            gen.writeNullField(name);
        } else {
            gen.writeStringField(name, str);
        }
    }

    /**
     * This class is a "hacky" way to return a MultiReddit's name and owner without actually having to send an HTTP
     * requests. This class extends MultiReddit, but the JsonNode will always be null. The only two methods that don't
     * return null are {@link #getOwner()} and {@link #getName()}. To get a MultiReddit that represents this class, you
     * can use {@code multiRedditManager.get(id.getOwner(), id.getName())}.
     */
    public static final class MultiRedditId extends MultiReddit {
        private final String owner;
        private final String name;

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
