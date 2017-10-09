package net.dean.jraw.example.script;

import net.dean.jraw.RedditClient;
import net.dean.jraw.Version;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Sorting;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.pagination.Paginator;

public final class ScriptExample {
    public static void main(String[] args) {
        // You'll want to change this for your specific OAuth2 app
        Credentials credentials =
            Credentials.script("jraw_test", "PYbdv4F78eWb7WKCqsZwqQMHq", "vgMHmurocRkiPg", "nto5hITmgDZg9IvJI9UltlL_Ejc");

        // Construct our NetworkAdapter
        UserAgent userAgent = new UserAgent("bot", "net.dean.jraw.example.script", Version.get(), "thatJavaNerd");
        NetworkAdapter http = new OkHttpNetworkAdapter(userAgent);

        // Authenticate our client
        RedditClient reddit = OAuthHelper.automatic(http, credentials);

        // Browse through the top posts of the last month, requesting as much data as possible per request
        DefaultPaginator<Submission> paginator = reddit.frontPage()
            .limit(Paginator.RECOMMENDED_MAX_LIMIT)
            .sorting(Sorting.TOP)
            .timePeriod(TimePeriod.MONTH)
            .build();

        // Request the first page
        Listing<Submission> firstPage = paginator.next();

        for (Submission post : firstPage) {
            if (post.getDomain().contains("imgur.com")) {
                System.out.println(String.format("%s (/r/%s, %s points) - %s",
                    post.getTitle(), post.getSubreddit(), post.getScore(), post.getUrl()));
            }
        }
    }
}
