package net.dean.jraw.docs.samples;

import net.dean.jraw.RedditClient;
import net.dean.jraw.docs.CodeSample;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Account;
import net.dean.jraw.oauth.*;

import java.util.UUID;

@SuppressWarnings("unused")
final class OAuth2 {
    @CodeSample
    void automatic(NetworkAdapter networkAdapter, Credentials scriptOrUserlessCredentials) {
        // Use Credentails.script, Credentials.userless, or Credentials.userlessApp
        RedditClient reddit = OAuthHelper.automatic(networkAdapter, scriptOrUserlessCredentials);
    }

    @CodeSample
    void interactive(UserAgent userAgent, Credentials installedOrWebCredentials) {
        NetworkAdapter networkAdapter = new OkHttpNetworkAdapter(userAgent);
        final StatefulAuthHelper helper =
            OAuthHelper.interactive(networkAdapter, installedOrWebCredentials);

        // 1. Generate the authorization URL. This will request a refresh token (1st parameter),
        // use the mobile site (2nd parameter), and request the "read" and "vote" scopes (all
        // other parameters).
        String authUrl = helper.getAuthorizationUrl(true, true, "read", "vote");

        // This class mirrors the Android WebView API fairly closely
        final MockWebView browser = new MockWebView();

        // 2. Show the user the authorization URL
        browser.loadUrl(authUrl);

        // Listen for pages starting to load
        browser.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(String url) {
                // 3. Listen for the final redirect URL.
                if (helper.isFinalRedirectUrl(url)) {
                    // 4. Verify the data and request our tokens. Note that onUserChallenge
                    // performs an HTTP request so Android apps will have to use an AsyncTask
                    // or Observable.
                    RedditClient reddit = helper.onUserChallenge(url);

                    // We now have access to an authenticated RedditClient! Test it out:
                    Account me = reddit.me().about();
                }
            }
        });
    }

    @CodeSample
    void requestRefreshToken(StatefulAuthHelper statefulAuthHelper) {
        boolean requestRefreshToken = true;
        boolean useMobileSite = true;
        String[] scopes = { "account", "edit", "flair" };
        String authUrl =
            statefulAuthHelper.getAuthorizationUrl(requestRefreshToken, useMobileSite, scopes);
    }

    @CodeSample
    void tokenStoreParameter(NetworkAdapter networkAdapter, Credentials credentials, TokenStore tokenStore) {
        // Automatic authentication
        RedditClient redditClient =
            OAuthHelper.automatic(networkAdapter, credentials, tokenStore);

        // Or interactive authentication
        StatefulAuthHelper authHelper =
            OAuthHelper.interactive(networkAdapter, credentials, tokenStore);
    }

    @CodeSample
    void revokeTokens(RedditClient redditClient) {
        // Revoke the access token
        redditClient.getAuthManager().revokeAccessToken();

        // Revoke the refresh token
        redditClient.getAuthManager().revokeRefreshToken();
    }

    @CodeSample
    void accountHelper(NetworkAdapter networkAdapter, Credentials credentials,
                                      TokenStore tokenStore, UUID deviceUuid) {
        AccountHelper helper =
            new AccountHelper(networkAdapter, credentials, tokenStore, deviceUuid);

        // When we first load up the app, let's use userless mode so the user can still read the
        // front page, etc.
        RedditClient reddit = helper.switchToUserless();
        // AccountHelper always maintains a reference to the active RedditClient
        assert helper.getReddit() == reddit;

        // The user has decided to log in. Let's see if we already have a refresh token or an
        // unexpired access token on hand
        String username = "foo"; // for the sake of example

        try {
            // We could avoid try/catch by using trySwitchToUser, which returns null instead of
            // throwing an Exception.
            helper.switchToUser(username);
        } catch (IllegalStateException e) {
            // There wasn't any data we could use, we have to have the user allow our app access
            // to their account
            StatefulAuthHelper statefulAuthHelper = helper.switchToNewUser();

            // (do the auth process as described in the "Interactive Authentication" section)

            String finalUrl = "bar"; // for the sake of example
            statefulAuthHelper.onUserChallenge(finalUrl);
        }

        reddit = helper.getReddit();

        // We should have some data stored now!
        assert tokenStore.fetchCurrent(username) != null;
        assert reddit.getAuthManager().getAccessToken()
            .equals(tokenStore.fetchCurrent(username).getAccessToken());

        // Repeat this process whenever we have a new user

        // Now let's say the user logs out:
        reddit = helper.switchToUserless();
    }

    @CodeSample
    void manualRenewing(RedditClient redditClient) {
        // Disable auto renew
        redditClient.setAutoRenew(false);

        // Manually renew the access token
        AuthManager authManager = redditClient.getAuthManager();
        if (authManager.canRenew()) {
            authManager.renew();
        }
    }

    private static final class MockWebView {
        void loadUrl(String url) {}
        void setWebViewClient(WebViewClient listener) {}
    }

    private interface WebViewClient {
        void onPageStarted(String url);
    }
}
