package net.dean.jraw.test.auth;

import net.dean.jraw.http.NetworkException;
import net.dean.jraw.managers.InboxManager;
import net.dean.jraw.models.Message;
import net.dean.jraw.pagination.InboxPaginator;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class InboxManagerTest extends AuthenticatedRedditTest {
    private InboxManager inbox;

    public InboxManagerTest() {
        this.inbox = new InboxManager(reddit);
    }

    @Test
    public void testRead() {
        try {
            InboxPaginator paginator = inbox.iterate(InboxPaginator.Where.INBOX);
            Message m1 = paginator.next().get(0);
            boolean expected = !m1.isRead();

            inbox.setRead(m1, expected);

            paginator.reset();
            Message m2 = paginator.next().get(0);
            assertEquals(m1.getFullName(), m2.getFullName());
            assertEquals(m2.isRead().booleanValue(), expected);
        } catch (NetworkException e) {
            handle(e);
        } catch (IllegalStateException e) {
            handle(e.getCause() != null ? e.getCause() : e);
        }
    }
}
