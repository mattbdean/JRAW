package net.dean.jraw.managers;

import net.dean.jraw.ApiException;
import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RestResponse;
import net.dean.jraw.http.HttpRequest;
import net.dean.jraw.models.MultiReddit;
import org.codehaus.jackson.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides the ability to create, read, update, and delete multireddits.
 */
public class MultiRedditManager extends AbstractManager {

    /**
     * Instantiates a new MultiRedditManager
     * @param client The RedditClient to use
     */
    public MultiRedditManager(RedditClient client) {
        super(client);
    }

    /**
     * Gets a list of your MultiReddits
     *
     * @return A list of your multireddits
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the Reddit API returned an error
     */
    @EndpointImplementation(Endpoints.MULTI_MINE)
    public List<MultiReddit> mine() throws NetworkException, ApiException {
        List<MultiReddit> multis = new ArrayList<>();
        JsonNode multiArray = reddit.execute(reddit.request()
                .endpoint(Endpoints.MULTI_MINE)
                .build()).getJson();
        checkForError(multiArray);

        for (JsonNode multi : multiArray) {
            multis.add(new MultiReddit(multi.get("data")));
        }

        return multis;
    }

    /**
     * Updates a multireddit or creates one if it does not exist
     *
     * @param name The name of the multireddit
     * @param subreddits The subreddits that make up this multireddit
     * @param priv If this multireddit is private
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the Reddit API returned an error
     * @return The updated MultiReddit
     */
    @EndpointImplementation({Endpoints.MULTI_MULTIPATH_PUT, Endpoints.MULTI_MULTIPATH_POST})
    public MultiReddit createOrUpdate(String name, List<String> subreddits, boolean priv) throws NetworkException, ApiException {

        MultiRedditJsonModel creationData = new MultiRedditJsonModel(subreddits, priv);

        Map<String, String> args = new HashMap<>();
        args.put("model", JrawUtils.toJson(creationData));

        HttpRequest.Builder request = reddit.request()
                .endpoint(Endpoints.MULTI_MULTIPATH_POST, getMultiPath(name).substring(1));

        request.put(args);

        RestResponse response = reddit.execute(request.build());
        JsonNode result = response.getJson();
        checkForError(result);
        return new MultiReddit(result.get("data"));
    }

    /**
     * Adds a subreddit to a multireddit.
     *
     * @param multiName The name of the multireddit
     * @param subreddit The subreddit to add
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.MULTI_MULTIPATH_R_SRNAME_PUT)
    public void addSubreddit(String multiName, String subreddit) throws NetworkException {
        MultiRedditSubredditModel data = new MultiRedditSubredditModel(subreddit);

        HttpRequest request = reddit.request()
                .endpoint(Endpoints.MULTI_MULTIPATH_R_SRNAME_PUT, multiName, subreddit)
                .put(JrawUtils.mapOf(
                        "model", JrawUtils.toJson(data),
                        "multipath", getMultiPath(multiName),
                        "srname", subreddit
                )).build();

        reddit.execute(request);
    }

    /**
     * Removes a subreddit from a multireddit
     *
     * @param multiName The name of the multireddit
     * @param subreddit The subreddit to remove
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.MULTI_MULTIPATH_R_SRNAME_DELETE)
    public void removeSubreddit(String multiName, String subreddit) throws NetworkException {
        HttpRequest request = reddit.request()
                .endpoint(Endpoints.MULTI_MULTIPATH_R_SRNAME_DELETE, getMultiPath(multiName).substring(1), subreddit)
                .query(
                        "multipath", getMultiPath(multiName),
                        "srname", subreddit
                ).delete()
                .build();

        reddit.execute(request);
    }

    /**
     * Copies a multireddit belonging to the authenticated user
     *
     * @param sourceName The name of the source multireddit
     * @param destName The name of the new multireddit
     * @throws NetworkException If the request was not successful. Most likely error code will be 409 Conflict, in which
     *                          case the authenticated user already has a multireddit of that name.
     * @throws ApiException If the Reddit API returned an error
     */
    public void copy(String sourceName, String destName) throws NetworkException, ApiException {
        copy(reddit.getAuthenticatedUser(), sourceName, destName);
    }

    /**
     * Copies a multireddit (granted you have rights to view it) to your own multireddits.
     *
     * @param sourceOwner The owner of the source multireddit
     * @param sourceMulti The name of the source multireddit
     * @param destName The name of the new multireddit
     * @throws NetworkException If the request was not successful. Most likely error code will be 409 Conflict, in which
     *                          case the authenticated user already has a multireddit of that name.
     * @throws ApiException If the Reddit API returned an error
     */
    @EndpointImplementation(Endpoints.MULTI_MULTIPATH_COPY)
    public void copy(String sourceOwner, String sourceMulti, String destName) throws NetworkException, ApiException {
        String from = getMultiPath(sourceOwner, sourceMulti);
        String to = getMultiPath(destName);
        HttpRequest request = reddit.request()
                // Using .endpoint(Endpoints.MULTI_MULTIPATH_COPY) returns 400 Bad Request, use this path instead.
                .path("/api/multi/copy")
                .post(JrawUtils.mapOf(
                        "from", from,
                        "to", to
                )).build();

        RestResponse response = reddit.execute(request);
        try {
            checkForError(response.getJson());
        } catch (ApiException e) {
            // For some reason the API responds with a 409 Conflict when the multi already exists, and a "MULTI_EXISTS"
            // API code else. The copied multi is still created.
            if (!e.getReason().equals("MULTI_EXISTS")) {
                throw e;
            }
        }
    }

    /**
     * Renames a multireddit
     *
     * @param prevName The original name of the multi
     * @param newName The new name of the multi
     * @throws NetworkException If the request was not successful
     *                          means that the user already has a multireddit of that name
     * @throws ApiException If the Reddit API returns an error
     */
    @EndpointImplementation(Endpoints.MULTI_MULTIPATH_RENAME)
    public void rename(String prevName, String newName) throws NetworkException, ApiException {
        String from = getMultiPath(prevName);
        String to = getMultiPath(newName);
        HttpRequest request = reddit.request()
                // Using .endpoint(Endpoints.MULTI_MULTIPATH_RENAME) returns 400 Bad Request, use this path instead.
                .path("/api/multi/rename")
                .post(JrawUtils.mapOf(
                        "from", from,
                        "to", to
                )).build();

        RestResponse response = reddit.execute(request);
        try {
            checkForError(response.getJson());
        } catch (ApiException e) {
            // For some reason the API responds with a "MULTI_NOT_FOUND" code even if the multi was successfully renamed
            if (!e.getReason().equals("MULTI_NOT_FOUND")) {
                throw e;
            }
        }
    }

    /**
     * Updates a multireddit's description
     *
     * @param multiName The name of the multireddit
     * @param newDescription The multireddit's new description, formatted in Markdown
     * @return A RenderStringPair representing the new description
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.MULTI_MULTIPATH_DESCRIPTION_PUT)
    public String updateDescription(String multiName, String newDescription) throws NetworkException {
        HttpRequest request = reddit.request()
                .endpoint(Endpoints.MULTI_MULTIPATH_DESCRIPTION_PUT, getMultiPath(multiName).substring(1))
                .put(JrawUtils.mapOf(
                        "model", String.format("{\"body_md\":\"%s\"}", newDescription),
                        "multipath", getMultiPath(multiName)
                )).build();
        RestResponse response = reddit.execute(request);
        JsonNode dataNode = response.getJson().get("data");

        return dataNode.get("body_md").asText();
    }

    /**
     * Deletes a multireddit. Since the HTTP request that this method sends does not return any body data, it can be assumed
     * that this operation completed successfully if no NetworkException was thrown.
     *
     * @param name The name of the multireddit
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.MULTI_MULTIPATH_DELETE)
    public void delete(String name) throws NetworkException {
        HttpRequest request = reddit.request()
                .endpoint(Endpoints.MULTI_MULTIPATH_DELETE, getMultiPath(name).substring(1))
                .delete()
                .build();
        reddit.execute(request).getJson();

        // This endpoint does not return any JSON data, so we only have the HTTP code to go off of.
    }

    /**
     * Gets one of the logged-in user's multireddits
     *
     * @param name The name of the multireddit
     * @return A MultiReddit owned by the current user
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the multi does not exist
     */
    public MultiReddit get(String name) throws NetworkException, ApiException {
        return get(reddit.getAuthenticatedUser(), name);
    }

    /**
     * Gets a publicly available MultiReddit
     * @param owner The owner of the multireddit
     * @param multiName The name of the multireddit
     * @return A MultiReddit
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the Reddit API returned an error
     */
    @EndpointImplementation({
            Endpoints.MULTI_MULTIPATH_GET,
            Endpoints.MULTI_MULTIPATH_R_SRNAME_GET
    })
    public MultiReddit get(String owner, String multiName) throws NetworkException, ApiException {
        JsonNode node = reddit.execute(reddit.request()
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
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the Reddit API returned an error
     */
    @EndpointImplementation(Endpoints.MULTI_MULTIPATH_DESCRIPTION_GET)
    public String getDescription(String multiName) throws NetworkException, ApiException {
        return getDescription(reddit.getAuthenticatedUser(), multiName);
    }

    /**
     * Fetches the description of a public or self-owned multireddit.
     *
     * @param owner The owner of the multireddit
     * @param multiName The name of the multireddit
     * @return A String array in which the first index is Markdown and the second is HTML
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the Reddit API returned an error
     */
    @EndpointImplementation(Endpoints.MULTI_MULTIPATH_DESCRIPTION_GET)
    public String getDescription(String owner, String multiName) throws NetworkException, ApiException {
        JsonNode node = reddit.execute(reddit.request()
                .endpoint(Endpoints.MULTI_MULTIPATH_DESCRIPTION_GET, getMultiPath(owner, multiName).substring(1))
                .build()).getJson();

        checkForError(node);
        node = node.get("data");
        return node.get("body_md").asText();
    }

    /**
     * Gets the path to a multi in this format: {@code /user/{username}/m/{multiname}} where {@code username} is the
     * currently logged in user. If this method is being used in conjunction with
     * {@link HttpRequest.Builder#endpoint(Endpoints, String...)}, then it is recommended to call
     * {@code .substring(1)} on the return value because without it, the resulting URL would have double forward slashes.
     * For example: {@code http://www.reddit.com/api/multi//user/USERNAME/m/MULTI_NAME}
     *
     * @param multiName The name of the multireddit
     * @return The multireddit's path
     */
    private String getMultiPath(String multiName) {
        return getMultiPath(reddit.getAuthenticatedUser(), multiName);
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
