package net.dean.jraw.managers;

import com.fasterxml.jackson.databind.JsonNode;
import net.dean.jraw.ApiException;
import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.HttpRequest;
import net.dean.jraw.http.MultiRedditUpdateRequest;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RestResponse;
import net.dean.jraw.models.MultiReddit;
import net.dean.jraw.models.MultiSubreddit;

import java.util.ArrayList;
import java.util.List;

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
                .query("expand_srs", "true")
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
     * @param updateData The request to be sent
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the Reddit API returned an error
     * @return The updated MultiReddit. Note that the only non-null property in its list of {@link MultiSubreddit}s will
     *         be their names.
     */
    @EndpointImplementation({Endpoints.MULTI_MULTIPATH_PUT, Endpoints.MULTI_MULTIPATH_POST})
    public MultiReddit createOrUpdate(MultiRedditUpdateRequest updateData) throws NetworkException, ApiException {
        HttpRequest.Builder request = reddit.request()
                .endpoint(Endpoints.MULTI_MULTIPATH_POST, getMultiPath(updateData.getName()).substring(1))
                .put(JrawUtils.mapOf(
                        "model", JrawUtils.toJson(updateData),
                        "expand_srs", "true"
                ));

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
        MultiRedditUpdateRequest.SubredditModel data = new MultiRedditUpdateRequest.SubredditModel(subreddit);

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
        if (!reddit.hasActiveUserContext())
            throw new IllegalStateException("Cannot set the flair for self because there is no active user context");
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
        reddit.execute(request);

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
        if (!reddit.hasActiveUserContext())
            throw new IllegalStateException("Cannot set the flair for self because there is no active user context");
        return get(reddit.getAuthenticatedUser(), name);
    }

    /**
     * Gets a publicly available MultiReddit
     *
     * @param owner The owner of the multireddit
     * @param multiName The name of the multireddit
     * @return A MultiReddit
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the Reddit API returned an error
     */
    @EndpointImplementation({
            Endpoints.MULTI_MULTIPATH_GET,
            Endpoints.MULTI_MULTIPATH_R_SRNAME_GET,
            Endpoints.MULTI_MULTIPATH_DESCRIPTION_GET
    })
    public MultiReddit get(String owner, String multiName) throws NetworkException, ApiException {
        JsonNode node = reddit.execute(reddit.request()
                .endpoint(Endpoints.MULTI_MULTIPATH_GET, getMultiPath(owner, multiName).substring(1))
                .query("expand_srs", "true")
                .build()).getJson();

        checkForError(node);
        return new MultiReddit(node.get("data"));
    }

    /** Gets a user's public multireddits. */
    @EndpointImplementation(Endpoints.MULTI_USER_USERNAME)
    public List<MultiReddit> getPublicMultis(String username) throws NetworkException {
        RestResponse response = reddit.execute(reddit.request()
                .endpoint(Endpoints.MULTI_USER_USERNAME, username)
                .query("expand_srs", "true")
                .build());
        JsonNode root = response.getJson();
        List<MultiReddit> multiReddits = new ArrayList<>(root.size());
        for (JsonNode node : root) {
            multiReddits.add(new MultiReddit(node.get("data")));
        }

        return multiReddits;
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
        if (!reddit.hasActiveUserContext())
            throw new IllegalStateException("Cannot get a self-owned multireddit's path because there is no active user context");
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
}
