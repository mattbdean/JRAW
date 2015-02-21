package net.dean.jraw.test;

import net.dean.jraw.ApiException;
import net.dean.jraw.Version;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.managers.InboxManager;
import net.dean.jraw.models.Contribution;
import net.dean.jraw.models.PrivateMessage;
import net.dean.jraw.paginators.InboxPaginator;
import org.testng.annotations.Test;

public class InboxManagerTest extends RedditTest {
    private InboxManager inbox;

    public InboxManagerTest() {
        this.inbox = new InboxManager(reddit);
    }

    @Test
    public void testRead() {
        try {
            InboxPaginator paginator = new InboxPaginator(reddit, "messages");

            Contribution m1 = paginator.next().get(0);
            if (m1 instanceof PrivateMessage) {
                PrivateMessage m = (PrivateMessage) m1;
                boolean expected = !m.isRead();
                inbox.setRead(m, expected);
            }
        } catch (NetworkException e) {
            handle(e);
        } catch (IllegalStateException e) {
            // e.getCause() might be a NetworkException
            handle(e.getCause() != null ? e.getCause() : e);
        }
    }

    @Test
    public void testReadAll() {
        try {
            // Will throw a NetworkException if not successful
            inbox.setAllRead();
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testCompose() {
        try {
            String subject = "InboxManagerTest for JRAW v" + Version.get().formatted();
            String body = "epoch=" + epochMillis();
            inbox.compose("/r/jraw_testing2", subject, body);
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }
}
