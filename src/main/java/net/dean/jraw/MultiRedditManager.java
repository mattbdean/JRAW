package net.dean.jraw;

import net.dean.jraw.http.HttpVerb;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RedditResponse;
import net.dean.jraw.http.RestRequest;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.MultiReddit;
import net.dean.jraw.models.RenderStringPair;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the ability to perform CRUD operations on multireddits.
 */
public class MultiRedditManager {
    private final LoggedInAccount account;
    private final RedditClient reddit;

    /**
     * Instantiates a new MultiRedditManager
     * @param account The account to use
     */
    public MultiRedditManager(LoggedInAccount account) {
        this.account = account;
        this.reddit = account.getCreator();
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
        JsonNode multiArray = reddit.execute(reddit.request(HttpVerb.GET, "/api/multi/mine")).getJson();
        checkForError(multiArray);

        for (JsonNode multi : multiArray) {
            multis.add(new MultiReddit(multi.get("data")));
        }

        return multis;
    }

    /**
     * Creates a new multireddit
     *
     * @param name The name of the new multireddit
     * @param subreddits The subreddits that make up this multireddit
     * @param priv If this multireddit is private
     * @throws NetworkException If the status code was not 201 Created
     * @throws ApiException If a multireddit of that name already exists
     */
    @EndpointImplementation(Endpoints.MULTI_MULTIPATH_POST)
    public void create(String name, List<String> subreddits, boolean priv) throws NetworkException, ApiException {
        StringWriter out = new StringWriter();
        try {
            JsonGenerator jgen = new JsonFactory().createJsonGenerator(out);

            /*
            {
                "subreddits": [
                    {
                        "name": <sub>
                    },
                    ...
                ],
                "visibility": ["public" | "private"]
            }
             */
            jgen.writeStartObject();
            jgen.writeFieldName("subreddits");
            jgen.writeStartArray();

            // Write the subreddits array
            for (String sub : subreddits) {
                jgen.writeStartObject();
                jgen.writeStringField("name", sub);
                jgen.writeEndObject();
            }
            jgen.writeEndArray();

            jgen.writeStringField("visibility",
                    priv ? "private" : "public");
            jgen.writeEndObject();
            jgen.close();
        } catch (IOException e) {
            JrawUtils.logger().error("Could not create the JSON model", e);
        }

        String path = "/api/multi/" + name;
        try {
            RestRequest request = reddit.requestBuilder(HttpVerb.POST, path)
                    .args(
                            "model", out,
                            "multipath", getMultiPath(name)
                    ).build();

            RedditResponse response = reddit.execute(request);
            JsonNode result = response.getJson();
            JrawUtils.logger().info(result.asText());
            checkForError(result);
        } catch (NetworkException e) {
            if (e.getActualCode() == 409) {
                throw new ApiException(String.format("A MultiReddit named \"%s\" already exists", name), e);
            } else if (e.getActualCode() != 201) {
                throw e;
            }

            // Ignore as of this point, this endpoint responds with 201 Created if successful
        }
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
        RestRequest request = reddit.request(HttpVerb.DELETE, getFullPath(name));
        reddit.execute(request);
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
        String path = getFullPath(name);
        JsonNode json = reddit.execute(reddit.request(HttpVerb.GET, path)).getJson();
        checkForError(json);
        return new MultiReddit(json.get("data"));
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
        JsonNode node = reddit.execute(reddit.request(HttpVerb.GET, getMultiPath(owner, multiName))).getJson();
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
        String path = getFullPath(owner, multiName) + "/description";
        JsonNode node = reddit.execute(reddit.request(HttpVerb.GET, path)).getJson();
        checkForError(node);
        node = node.get("data");
        return new RenderStringPair(node.get("body_md").asText(), node.get("body_html").asText());
    }

    /**
     * Gets the path to a multi in this format: {@code /user/{username}/m/{multiname}} where {@code username} is the
     * currently logged in user.
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

    /**
     * Gets the full path to a multi in this format: {@code /api/multi/user/{username}/m/{multiname}} where {@code username}
     * is the currently logged in user.
     *
     * @param multiName The name of the multireddit
     * @return The full multireddit path
     */
    private String getFullPath(String multiName) {
        return getFullPath(account.getFullName(), multiName);
    }

    /**
     * Gets the full path to a multi in this format: {@code /api/multi/user/{username}/m/{multiname}}
     *
     * @param multiName The name of the multireddit
     * @return The full multireddit path
     */
    private String getFullPath(String owner, String multiName) {
        return String.format("/api/multi%s", getMultiPath(owner, multiName));
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
