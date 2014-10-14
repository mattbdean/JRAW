package net.dean.jraw.test.auth;

import net.dean.jraw.http.NetworkException;
import net.dean.jraw.managers.WikiManager;
import net.dean.jraw.models.WikiPage;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Tests anything and everything related to wikis.
 */
public class WikiManagerTest extends AuthenticatedRedditTest {
    private WikiManager manager;

    public WikiManagerTest() {
        this.manager = new WikiManager(reddit);
    }

    @Test
    public void testFrontWikiPages() throws NetworkException {
        testWikiPages(null);
    }

    @Test
    public void testSubredditWikiPages() throws NetworkException {
        String sub = "todayilearned";
        testWikiPages(sub);
    }

    private void testWikiPages(String subreddit) throws NetworkException {
        int limit = 3;

        List<String> pages = manager.getWikiPages(subreddit);
        int counter = 0;
        for (String page : pages) {
            WikiPage wikiPage = manager.getWikiPage(subreddit, page);
            validateModel(wikiPage);

            counter++;
            if (counter >= limit) {
                break;
            }
        }
    }
}
