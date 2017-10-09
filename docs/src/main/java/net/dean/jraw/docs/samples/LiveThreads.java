package net.dean.jraw.docs.samples;

import net.dean.jraw.RedditClient;
import net.dean.jraw.docs.CodeSample;
import net.dean.jraw.models.*;
import net.dean.jraw.pagination.BarebonesPaginator;
import net.dean.jraw.references.LiveThreadReference;
import net.dean.jraw.websocket.LiveThreadListener;
import net.dean.jraw.websocket.ReadOnlyWebSocketHelper;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
final class LiveThreads {
    @CodeSample
    void create(RedditClient redditClient) {
        LiveThreadPatch initialSettings = new LiveThreadPatch.Builder()
            .description("a description of what's going on")
            .nsfw(false)
            .resources("some external links for viewers of the thread")
            .title("test thread")
            .build();

        LiveThreadReference createdThread = redditClient.me()
            .createLiveThread(initialSettings);
    }

    @CodeSample
    void byId(RedditClient redditClient) {
        LiveThreadReference someLiveThread =
            redditClient.liveThread("<ID goes here>");
    }

    @CodeSample
    void happeningNow(RedditClient redditClient) {
        // If there's no featured thread, this will return null
        LiveThread liveThread = redditClient.happeningNow();

        if (liveThread != null) {
            LiveThreadReference reference = liveThread.toReference(redditClient);
            // do something with reference (probably read its updates)
        }
    }

    @CodeSample
    void readUpdates(LiveThreadReference liveThreadReference) {
        BarebonesPaginator.Builder<LiveUpdate> paginatorBuilder = liveThreadReference.latestUpdates();
        BarebonesPaginator<LiveUpdate> paginator = paginatorBuilder.limit(25).build();

        // Fetch up to the last 25 updates
        Listing<LiveUpdate> latestUpdates = paginator.next();
    }

    @CodeSample
    void postUpdate(LiveThreadReference liveThreadReference) {
        liveThreadReference.postUpdate("Something just happened!");
    }

    @CodeSample
    void strikeAndDeleteUpdate(LiveThreadReference liveThreadReference, LiveUpdate liveUpdate) {
        // This update is incorrect, strike it
        liveThreadReference.strikeUpdate(liveUpdate.getId());

        // This update needs to be permanently removed from the thread
        liveThreadReference.deleteUpdate(liveUpdate.getId());
    }

    @CodeSample
    void close(LiveThreadReference liveThreadReference) {
        liveThreadReference.close();
    }

    @CodeSample
    void websocket(LiveThreadReference liveThreadReference) {
        LiveThreadListener listener = new LiveThreadListener() {
            @Override
            public void onUpdate(@NotNull LiveWebSocketUpdate update) {
                // do something with the update
            }
        };

        // Open a connection to the live thread's WebSocket and react to updates with listener
        ReadOnlyWebSocketHelper webSocketHelper = liveThreadReference.liveUpdates(listener);

        // When we don't want to listen to any more updates, make sure to close the connection
        webSocketHelper.close(ReadOnlyWebSocketHelper.CLOSE_CODE_DEFAULT);
    }
}
