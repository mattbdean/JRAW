package net.dean.jraw.managers;

import com.fasterxml.jackson.databind.JsonNode;
import net.dean.jraw.ApiException;
import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.RestResponse;
import net.dean.jraw.models.LiveThread;
import net.dean.jraw.models.LiveUpdate;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.LiveThreadPaginator;
import net.dean.jraw.paginators.Paginator;

import java.util.ArrayList;
import java.util.List;

import static net.dean.jraw.util.JrawUtils.mapOf;

/**
 * This class is responsible for managing live threads.
 */
public class LiveThreadManager extends AbstractManager {
    /**
     * Instantiates a new LiveThreadManager
     *
     * @param reddit The RedditClient to use
     */
    public LiveThreadManager(RedditClient reddit) {
        super(reddit);
    }

    /**
     * Creates a new LiveThread
     *
     * @param nsfw Is the thread Not Safe For Work?
     * @return The newly-created LiveThread
     * @throws ApiException If there was a problem creating the thread
     */
    public LiveThread create(String title, boolean nsfw, String description) throws ApiException {
        return create(title, nsfw, description, "");
    }

    /**
     * Creates a new LiveThread
     *
     * @param nsfw Is the thread Not Safe For Work?
     * @param resources An additional information
     * @return The newly-created LiveThread
     * @throws ApiException If there was a problem creating the thread
     */
    @EndpointImplementation(Endpoints.LIVE_CREATE)
    public LiveThread create(String title, boolean nsfw, String description, String resources) throws ApiException {
        RestResponse response = genericPost(reddit.request()
                        .endpoint(Endpoints.LIVE_CREATE)
                        .post(mapOf(
                                "api_type", "json",
                                "description", description,
                                "nsfw", nsfw,
                                "resources", resources,
                                "title", title
                        ))
                        .build()
        );
        return get(response.getJson().get("json").get("data").get("id").asText());
    }

    /**
     * Edits the metadata of the thread, such as the title and description. The authenticated user must have the
     * 'settings' permission on the thread.
     *
     * @param thread The thread to alter
     * @param nsfw Is the thread Not Safe For Work?
     * @param resources An additional information
     * @throws ApiException
     */
    @EndpointImplementation(Endpoints.LIVE_THREAD_EDIT)
    public void edit(LiveThread thread, String title, boolean nsfw, String description, String resources) throws ApiException {
        genericPost(reddit.request()
                        .endpoint(Endpoints.LIVE_THREAD_EDIT, thread.getId())
                        .post(mapOf(
                                "api_type", "json",
                                "description", description,
                                "nsfw", nsfw,
                                "resources", resources,
                                "title", title
                        ))
                        .build()
        );
    }

    /** Posts an update to a thread */
    @EndpointImplementation(Endpoints.LIVE_THREAD_UPDATE)
    public void postUpdate(LiveThread thread, String body) throws ApiException {
        genericPost(reddit.request()
                .endpoint(Endpoints.LIVE_THREAD_UPDATE, thread.getId())
                .post(mapOf(
                        "api_type", "json",
                        "body", body
                ))
                .build());
    }

    /** Deletes an update. The given LiveUpdate must belong in the LiveThread. */
    @EndpointImplementation(Endpoints.LIVE_THREAD_DELETE_UPDATE)
    public void deleteUpdate(LiveThread host, LiveUpdate update) throws ApiException {
        genericPost(reddit.request()
                .endpoint(Endpoints.LIVE_THREAD_DELETE_UPDATE, host.getId())
                .post(mapOf(
                        "api_type", "json",
                        "id", update.getFullName()
                ))
                .build());
    }

    /** Strikes an update so that it will appear <del>like this</del> on the website. */
    @EndpointImplementation(Endpoints.LIVE_THREAD_STRIKE_UPDATE)
    public void strikeUpdate(LiveThread host, LiveUpdate update) throws ApiException {
        genericPost(reddit.request()
                .endpoint(Endpoints.LIVE_THREAD_DELETE_UPDATE, host.getId())
                .post(mapOf(
                        "api_type", "json",
                        "id", update.getFullName()
                ))
                .build());
    }

    /** Gets a list of users who have permissions in this thread */
    @EndpointImplementation(Endpoints.LIVE_THREAD_CONTRIBUTORS)
    public List<LiveThread.Contributor> getContributors(LiveThread thread) {
        RestResponse response = reddit.execute(reddit.request()
                .endpoint(Endpoints.LIVE_THREAD_CONTRIBUTORS, thread.getId())
                .build());
        JsonNode node = response.getJson().get(0).get("data").get("children");

        List<LiveThread.Contributor> contributors = new ArrayList<>(node.size());
        for (JsonNode n : node) {
            contributors.add(new LiveThread.Contributor(n));
        }

        return contributors;
    }

    /** Gets a Paginator that iterates over Submissions that link to this thread */
    @EndpointImplementation(Endpoints.LIVE_THREAD_DISCUSSIONS)
    public Paginator<Submission> getDiscussions(final LiveThread subject) {
        return new Paginator<Submission>(reddit, Submission.class) {
            @Override
            protected String getBaseUri() {
                return "/api/live/" + subject.getId() + "/discussions";
            }
        };
    }

    /** Gets a live thread by its ID */
    @EndpointImplementation(Endpoints.LIVE_THREAD_ABOUT)
    public LiveThread get(String id) {
        return reddit.execute(reddit.request()
                .endpoint(Endpoints.LIVE_THREAD_ABOUT, id)
                .build()).as(LiveThread.class);
    }

    /** Gets a Paginator that will iterate through the updates of a LiveThread */
    public LiveThreadPaginator stream(LiveThread thread) {
        return new LiveThreadPaginator(reddit, thread.getId());
    }
}
