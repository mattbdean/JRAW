package net.dean.jraw.managers;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RestRequest;
import net.dean.jraw.models.WikiPage;
import org.codehaus.jackson.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class WikiManager extends AbstractManager {
    public WikiManager(RedditClient client) {
        super(client);
    }

    @Override
    protected boolean requiresAuthentication(RestRequest r) {
        return true;
    }


    /**
     * Gets a list of names of wiki pages for Reddit
     * @return A list of Reddit's wiki pages
     * @throws NetworkException If there was a problem sending the HTTP request
     */
    public List<String> getWikiPages() throws NetworkException {
        return getWikiPages(null);
    }

    /**
     * Gets a list of names of wiki pages for a certain subreddit
     * @param subreddit The subreddit to use
     * @return A list of wiki pages for this subreddit
     * @throws NetworkException If there was a problem sending the HTTP request
     */
    @EndpointImplementation(Endpoints.WIKI_PAGES)
    public List<String> getWikiPages(String subreddit) throws NetworkException {
        String path = JrawUtils.getSubredditPath(subreddit, "/wiki/pages.json");

        List<String> pages = new ArrayList<>();
        JsonNode pagesNode = execute(request()
                .path(path)
                .build()).getJson().get("data");

        for (JsonNode page : pagesNode) {
            pages.add(page.asText());
        }

        return pages;
    }

    /**
     * Gets a WikiPage that represents one of Reddit's main pages. See <a href="http://www.reddit.com/wiki/pages">here</a>
     * for a list.
     *
     * @param page The page to get
     * @return A WikiPage for the given page
     * @throws NetworkException If there was a problem sending the HTTP request
     *
     * @see #getWikiPages()
     */
    public WikiPage getWikiPage(String page) throws NetworkException {
        return getWikiPage(null, page);
    }

    /**
     * Gets a WikiPage for a certain subreddit
     * @param subreddit The subreddit to use
     * @param page The page to get
     * @return A WikiPage for the given page
     * @throws NetworkException If there was a problem sending the HTTP request
     */
    @EndpointImplementation(Endpoints.WIKI_PAGE)
    public WikiPage getWikiPage(String subreddit, String page) throws NetworkException {
        String path = JrawUtils.getSubredditPath(subreddit, "/wiki/" + page + ".json");

        RestRequest r = request()
                .path(path)
                .build();
        return execute(r).as(WikiPage.class);
    }


}
