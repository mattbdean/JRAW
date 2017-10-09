package net.dean.jraw.docs.samples;

import net.dean.jraw.RedditClient;
import net.dean.jraw.docs.CodeSample;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;

@SuppressWarnings("unused")
final class Quickstart {
    @CodeSample
    void userAgent() {
        UserAgent userAgent = new UserAgent("bot", "com.example.usefulbot", "v0.1", "mattbdean");
    }

    @CodeSample
    void authenticate(UserAgent userAgent) {
        // Create our credentials
        Credentials credentials = Credentials.script("<username>", "<password>",
            "<client ID>", "<client secret>");

        // This is what really sends HTTP requests
        NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);

        // Authenticate and get a RedditClient instance
        RedditClient reddit = OAuthHelper.automatic(adapter, credentials);
    }
}
