package net.dean.jraw.test;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.WikiPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

public class WikiTest {
    private static RedditClient reddit;

    @BeforeClass
    public static void setUp() {
        reddit = TestUtils.client(WikiTest.class);
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

        List<String> pages = reddit.getWikiPages(subreddit);
        int counter = 0;
        for (String page : pages) {
            WikiPage wikiPage = reddit.getWikiPage(subreddit, page);
            ThingFieldTest.fieldValidityCheck(wikiPage);

            counter++;
            if (counter >= limit) {
                break;
            }
        }
    }
}
