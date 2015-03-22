package net.dean.jraw.test;

import net.dean.jraw.ApiException;
import net.dean.jraw.managers.LiveThreadManager;
import net.dean.jraw.models.LiveThread;
import net.dean.jraw.models.LiveUpdate;
import org.testng.annotations.Test;

public class LiveThreadManagerTest extends RedditTest {
    private final LiveThreadManager manager;
    private final LiveThread thread;

    public LiveThreadManagerTest() {
        this.manager = new LiveThreadManager(reddit);
        this.thread = initThread();
    }

    private LiveThread initThread() {
        try {
            return manager.create("Title", false, "Description", "Other resources");
        } catch (ApiException e) {
            handle(e);
        }
        return null;
    }

    @Test
    public void testContributors() {
        validateModels(manager.getContributors(thread));
    }

    @Test
    public void testDiscussions() {
        validateModels(manager.getDiscussions(thread));
    }

    @Test
    public void testEdit() {
        try {
            manager.edit(thread, "New title", true, "New description", "New resources");
        } catch (ApiException e) {
            handle(e);
        }
    }

    @Test
    public void testPostStrikeDeleteUpdate() {
        try {
            manager.postUpdate(thread, "New update!");
            LiveUpdate latestUpdate = manager.stream(thread.getId()).next().get(0);
            manager.strikeUpdate(thread, latestUpdate);
            manager.deleteUpdate(thread, latestUpdate);
        } catch (ApiException e) {
            handle(e);
        }
    }
}
