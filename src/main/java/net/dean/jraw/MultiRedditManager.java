package net.dean.jraw;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import net.dean.jraw.http.NetworkAccessible;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RedditResponse;
import net.dean.jraw.http.RequestBuilder;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.MultiReddit;
import net.dean.jraw.models.RenderStringPair;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides the ability to perform CRUD operations on multireddits.
 */
public class MultiRedditManager implements NetworkAccessible<RedditResponse, RedditClient> {
    private final LoggedInAccount account;
    private final RedditClient reddit;
    private final ObjectMapper objectMapper;

    /**
     * Instantiates a new MultiRedditManager
     * @param account The account to use
     */
    public MultiRedditManager(LoggedInAccount account) {
        this.account = account;
        this.reddit = account.getCreator();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Gets a list of your MultiReddits
     *
     * @return A list of your multireddits
     * @throws NetworkException If there was a problem sending the request
     * @throws ApiException If the Reddit API returned an error
     */
    @EndpointImplementation(Endpoints.MULTI_MINE)
    public List<MultiReddit> mine() throws NetworkException, ApiException {
        List<MultiReddit> multis = new ArrayList<>();
        JsonNode multiArray = execute(request()
                .endpoint(Endpoints.MULTI_MINE)
                .build()).getJson();
        checkForError(multiArray);

        for (JsonNode multi : multiArray) {
            multis.add(new MultiReddit(multi.get("data")));
        }

        return multis;
    }

    /**
     * Updates a multireddit, or creates one if it does not exist
     *
     * @param name The name of the multireddit
     * @param subreddits The subreddits that make up this multireddit
     * @param priv If this multireddit is private
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the Reddit API returned an error
     */
    @EndpointImplementation(Endpoints.MULTI_MULTIPATH_PUT)
    public void update(String name, List<String> subreddits, boolean priv) throws NetworkException, ApiException {
        updateMultiReddit(false, name, subreddits, priv);
    }

    /**
     * Adds a subreddit to a multireddit.
     *
     * @param multiName The name of the multireddit
     * @param subreddit The subreddit to add
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the Reddit API returned an error
     */
    @EndpointImplementation(Endpoints.MULTI_MULTIPATH_R_SRNAME_PUT)
    public void addSubreddit(String multiName, String subreddit) throws NetworkException, ApiException {
        MultiRedditSubredditModel data = new MultiRedditSubredditModel(subreddit);

        Request request = request()
                .endpoint(Endpoints.MULTI_MULTIPATH_R_SRNAME_PUT, multiName, subreddit)
                .put(new FormEncodingBuilder()
                        .add("model", toJson(data))
                        .add("multipath", getMultiPath(multiName))
                        .add("srname", subreddit)
                        .build())
                .build();

        execute(request);
    }

    /**
     * Removes a subreddit from a multireddit
     *
     * @param multiName The name of the multireddit
     * @param subreddit The subreddit to remove
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the Reddit API returned an error
     */
    @EndpointImplementation(Endpoints.MULTI_MULTIPATH_R_SRNAME_DELETE)
    public void removeSubreddit(String multiName, String subreddit) throws NetworkException, ApiException {
        Request request = request()
                .endpoint(Endpoints.MULTI_MULTIPATH_R_SRNAME_DELETE, getMultiPath(multiName).substring(1), subreddit)
                .query(
                        "multipath", getMultiPath(multiName),
                        "srname", subreddit
                ).delete()
                .build();

        execute(request);
    }

    @EndpointImplementation(Endpoints.MULTI_MULTIPATH_COPY)
    public void copy(String sourceOwner, String sourceMulti, String destName) throws NetworkException, ApiException {
        String from = getMultiPath(sourceOwner, sourceMulti);
        String to = getMultiPath(destName);
        Request request = request()
                // Using .endpoint(Endpoints.MULTI_MULTIPATH_COPY) returns 400 Bad Request, use this path instead.
                .path("/api/multi/copy")
                .post(new FormEncodingBuilder()
                        .add("from", from)
                        .add("to", to)
                        .build())
                .build();

        RedditResponse response = execute(request);
        try {
            checkForError(response.getJson());
        } catch (ApiException e) {
            // For some reason the API responds with a 409 Conflict when the multi already exists, and a "MULTI_EXISTS"
            // API code else. The copied multi is still created.
            if (!e.getCode().equals("MULTI_EXISTS")) {
                throw e;
            }
        }
    }

    @EndpointImplementation(Endpoints.MULTI_MULTIPATH_RENAME)
    public void rename(String prevName, String newName) throws NetworkException, ApiException {
        String from = getMultiPath(prevName);
        String to = getMultiPath(newName);
        Request request = request()
                // Using .endpoint(Endpoints.MULTI_MULTIPATH_RENAME) returns 400 Bad Request, use this path instead.
                .path("/api/multi/rename")
                .post(new FormEncodingBuilder()
                        .add("from", from)
                        .add("to", to)
                        .build())
                .build();

        RedditResponse response = execute(request);
        try {
            checkForError(response.getJson());
        } catch (ApiException e) {
            // For some reason the API responds with a "MULTI_NOT_FOUND" code even if the multi was renamed
            if (!e.getCode().equals("MULTI_NOT_FOUND")) {
                throw e;
            }
        }
    }

    @EndpointImplementation(Endpoints.MULTI_MULTIPATH_DESCRIPTION_PUT)
    public RenderStringPair updateDescription(String multiName, String newDescription) throws NetworkException {
        Request request = request()
                .endpoint(Endpoints.MULTI_MULTIPATH_DESCRIPTION_PUT, getMultiPath(multiName))
                .put(new FormEncodingBuilder()
                        .add("model", String.format("{\"body_md\":\"%s\"}", newDescription))
                        .add("multipath", getMultiPath(multiName))
                        .build())
                .build();
        RedditResponse response = execute(request);
        JsonNode dataNode = response.getJson().get("data");

        return new RenderStringPair(dataNode.get("body_md").asText(), dataNode.get("body_html").asText());
    }

    /**
     * Creates a new multireddit
     *
     * @param name The name of the new multireddit
     * @param subreddits The subreddits that make up this multireddit
     * @param priv If this multireddit is private
     * @throws NetworkException If the request was not successful. If a 400 Bad Request is returned, then most likely the
     *                          name of the multireddit is invalid (over 20 characters or starts with underscore)
     * @throws ApiException If a multireddit of that name already exists
     */
    @EndpointImplementation(Endpoints.MULTI_MULTIPATH_POST)
    public void create(String name, List<String> subreddits, boolean priv) throws NetworkException, ApiException {
        updateMultiReddit(true, name, subreddits, priv);
    }

    /**
     * Creates or updates a multireddit
     * @param post True for a POST request, false for a PUT request. POST will only create, while PUT will update or create
     *             if one does not exist
     * @param name The name of the multireddit
     * @param subreddits The subreddits that make up this multireddit
     * @param priv If this multireddit is private
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the Reddit API returned an error
     */
    private void updateMultiReddit(boolean post, String name, List<String> subreddits, boolean priv)
        throws NetworkException, ApiException {

        MultiRedditJsonModel creationData = new MultiRedditJsonModel(subreddits, priv);

        RequestBody body = new FormEncodingBuilder()
                    .add("model", toJson(creationData))
                    .add("multipath", getMultiPath(name))
                    .build();

        RequestBuilder request = request()
                .endpoint(Endpoints.MULTI_MULTIPATH_POST, name);

        if (post) {
            request.post(body);
        } else {
            request.put(body);
        }

        RedditResponse response = execute(request.build());
        JsonNode result = response.getJson();
        checkForError(result);
    }

    /**
     * Deletes a multireddit. Since the HTTP request that this method sends does not return any body data, it can be assumed
     * that this operation completed successfully if no NetworkException was thrown.
     *
     * @param name The name of the multireddit
     * @throws NetworkException If the status code was not 200
     */
    @EndpointImplementation(Endpoints.MULTI_MULTIPATH_DELETE)
    public void delete(String name) throws NetworkException {
        Request request = request()
                .endpoint(Endpoints.MULTI_MULTIPATH_DELETE, getMultiPath(name).substring(1))
                .delete()
                .build();
        execute(request);
        // This endpoint does not return any JSON data, so we only have the HTTP code to go off of.
    }

    /**
     * Gets one of the logged-in user's multireddits
     *
     * @param name The name of the multireddit
     * @return A MultiReddit owned by the current user
     * @throws NetworkException If there was a problem sending a request
     * @throws ApiException If the multi does not exist
     */
    public MultiReddit get(String name) throws NetworkException, ApiException {
        return get(account.getFullName(), name);
    }

    /**
     * Gets a publicly available MultiReddit
     * @param owner The owner of the multireddit
     * @param multiName The name of the multireddit
     * @return A MultiReddit
     * @throws NetworkException If there was a problem making the request
     * @throws ApiException If the Reddit API returned an error
     */
    @EndpointImplementation({
            Endpoints.MULTI_MULTIPATH_GET,
            Endpoints.MULTI_MULTIPATH_R_SRNAME_GET
    })
    public MultiReddit get(String owner, String multiName) throws NetworkException, ApiException {
        JsonNode node = execute(request()
                .endpoint(Endpoints.MULTI_MULTIPATH_GET, getMultiPath(owner, multiName).substring(1))
                .build()).getJson();

        checkForError(node);
        return new MultiReddit(node.get("data"));
    }

    /**
     * Fetches the description of a self-owned multireddit.
     *
     * @param multiName The name of the multireddit
     * @return A String array in which the first index is Markdown and the second is HTML
     * @throws NetworkException If there was a problem sending the request
     * @throws ApiException If the Reddit API returned an error
     */
    @EndpointImplementation(Endpoints.MULTI_MULTIPATH_DESCRIPTION_GET)
    public RenderStringPair getDescription(String multiName) throws NetworkException, ApiException {
        return getDescription(account.getFullName(), multiName);
    }

    /**
     * Fetches the description of a public or self-owned multireddit.
     *
     * @param owner The owner of the multireddit
     * @param multiName The name of the multireddit
     * @return A String array in which the first index is Markdown and the second is HTML
     * @throws NetworkException If there was a problem sending the request
     * @throws ApiException If the Reddit API returned an error
     */
    @EndpointImplementation(Endpoints.MULTI_MULTIPATH_DESCRIPTION_GET)
    public RenderStringPair getDescription(String owner, String multiName) throws NetworkException, ApiException {
        JsonNode node = execute(request()
                .endpoint(Endpoints.MULTI_MULTIPATH_DESCRIPTION_GET, getMultiPath(owner, multiName).substring(1))
                .build()).getJson();

        checkForError(node);
        node = node.get("data");
        return new RenderStringPair(node.get("body_md").asText(), node.get("body_html").asText());
    }

    /**
     * Gets the path to a multi in this format: {@code /user/{username}/m/{multiname}} where {@code username} is the
     * currently logged in user. If this method is being used in conjunction with {@link net.dean.jraw.http.RequestBuilder#endpoint(Endpoints, String...)},
     * then it is recommended to call {@code .substring(1)} on the return value because without it, the resulting URL
     * would have double forward slashes. For example: {@code http://www.reddit.com/api/multi//user/USERNAME/m/MULTI_NAME}
     *
     * @param multiName The name of the multireddit
     * @return The multireddit's path
     */
    private String getMultiPath(String multiName) {
        return getMultiPath(account.getFullName(), multiName);
    }

    /**
     * Gets the path to a multi in this format: {@code /user/{username}/m/{multiname}}
     * @param owner The owner of the multireddit
     * @param multiName The name of the multireddit
     * @return The multireddit's path
     */
    private String getMultiPath(String owner, String multiName) {
        return String.format("/user/%s/m/%s", owner, multiName);
    }

    private String toJson(Object o) {
        StringWriter out = new StringWriter();
        try {
            objectMapper.writeValue(out, o);
        } catch (IOException e) {
            JrawUtils.logger().error("Unable to create the data model", e);
            return null;
        }

        return out.toString();
    }

    /**
     * Checks if there was an error returned by a /api/multi/* request, since those endpoints return errors differently
     * than the rest of the API
     *
     * @param root The root JsonNode
     * @throws ApiException If there is a visible error
     */
    private void checkForError(JsonNode root) throws ApiException {
        if (root.has("explanation") && root.has("reason")) {
            throw new ApiException(root.get("reason").asText(), root.get("explanation").asText());
        }
    }

    @Override
    public RedditClient getCreator() {
        return reddit;
    }

    /**
     * This class represents a Java object of the data sent to create a multireddit. When an instance of this class is
     * turned into a JSON string, the output will look like this:
     *
     * <pre>
     * {@code
     * {
     *     "subreddits": [
     *         {"name": <sub>},
     *         ...
     *     ],
     *     "visibility": ["public" | "private"]
     * }
     * }
     * </pre>
     */
    private static final class MultiRedditJsonModel {
        private final List<Map<String, String>> subreddits;
        private final String visibility;

        public MultiRedditJsonModel(List<String> subs, boolean priv) {
            this.visibility = priv ? "private" : "public";

            this.subreddits = new ArrayList<>(subs.size());
            for (String sub : subs) {
                HashMap<String, String> subredditMap = new HashMap<>();
                subredditMap.put("name", sub);
                subreddits.add(subredditMap);
            }
        }

        public List<Map<String, String>> getSubreddits() {
            return subreddits;
        }

        public String getVisibility() {
            return visibility;
        }
    }

    private static final class MultiRedditSubredditModel {
        private String name;

        private MultiRedditSubredditModel(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
