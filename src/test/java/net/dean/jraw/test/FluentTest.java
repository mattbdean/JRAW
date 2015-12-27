package net.dean.jraw.test;

import net.dean.jraw.fluent.FluentRedditClient;
import net.dean.jraw.paginators.TimePeriod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class FluentTest extends RedditTest {
    private FluentRedditClient api;

    public FluentTest() {
        super();
        this.api = new FluentRedditClient(reddit);
    }

    @Test
    public void testSubredditReference() {
        validateModel(api.frontPage().top(TimePeriod.HOUR).fetch());
        assertEquals(api.subreddit("pics")
                .limit(1)
                .fetch().size(), 1);
    }

    @Test
    public void testWikiReference() {
        validateModel(api.frontPage().wiki().get(api.frontPage().wiki().pages().get(2)));
    }

    @Test
    public void testGetSubscribed() {
        validateModels(api.me().subscribedSubreddits());
    }
}
