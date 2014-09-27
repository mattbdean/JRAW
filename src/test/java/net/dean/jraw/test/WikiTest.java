package net.dean.jraw.test;

import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.WikiPage;
import org.testng.annotations.Test;

import java.util.List;

public class WikiTest extends RedditTest {

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

        List<String> pages = reddit.getWikiPages(subreddit);
        int counter = 0;
        for (String page : pages) {
            WikiPage wikiPage = reddit.getWikiPage(subreddit, page);
            validateModel(wikiPage);

            counter++;
            if (counter >= limit) {
                break;
            }
        }
    }
}
