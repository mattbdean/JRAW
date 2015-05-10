package net.dean.jraw.test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import net.dean.jraw.RedditClient;
import net.dean.jraw.Version;
import net.dean.jraw.http.HttpRequest;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.InvalidScopeException;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.http.oauth.OAuthHelper;
import net.dean.jraw.paginators.SubredditPaginator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.testng.Assert.*;

public class OAuthHelperTest {
    private RedditClient reddit;
    private Credentials creds;

    @BeforeMethod
    public void setUp() {
        reddit = new RedditClient(UserAgent.of("desktop",
                "net.dean.jraw.test",
                Version.get().formatted(),
                "thatJavaNerd"));
        creds = CredentialsUtils.instance().installedApp();
    }

    @Test
    public void testAuthUrl() throws MalformedURLException {
        OAuthHelper helper = reddit.getOAuthHelper();
        HttpRequest expected = new HttpRequest.Builder()
                .https(true)
                .host(RedditClient.HOST_SPECIAL)
                .path("/api/v1/authorize")
                .query(
                        "client_id", "myClientId",
                        "response_type", "code",
                        "state", "untestable",
                        "redirect_uri", "http://www.example.com",
                        "duration", "permanent",
                        "scope", "scope1 scope2"
                ).build();
        HttpRequest actual = HttpRequest.from("GET", helper.getAuthorizationUrl(
                Credentials.webapp("myClientId", "", "http://www.example.com"), true, "scope1", "scope2"
        ));

        URL actualUrl = actual.getUrl();
        URL expectedUrl = expected.getUrl();
        assertEquals(actualUrl.getProtocol(), expectedUrl.getProtocol(), "Scheme was different");
        assertEquals(actualUrl.getHost(), expectedUrl.getHost(), "Host was different");
        assertEquals(actualUrl.getPath(), expectedUrl.getPath(), "Path was different");

        Map<String, String> actualQuery = parseQuery(actual.getUrl().getQuery());
        Map<String, String> expectedQuery = parseQuery(expected.getUrl().getQuery());

        for (Map.Entry<String, String> pair : expectedQuery.entrySet()) {
            if (pair.getKey().equals("state")) {
                // State is random
                continue;
            }
            assertEquals(pair.getValue(), actualQuery.get(pair.getKey()));
        }
    }

    @Test
    public void testMobileAuthUrl() throws MalformedURLException {
        OAuthHelper helper = reddit.getOAuthHelper();
        HttpRequest expected = new HttpRequest.Builder()
                .https(true)
                .host(RedditClient.HOST_SPECIAL)
                .path("/api/v1/authorize.compact")
                .query(
                        "client_id", "myClientId",
                        "response_type", "code",
                        "state", "untestable",
                        "redirect_uri", "http://www.example.com",
                        "duration", "permanent",
                        "scope", "scope1 scope2"
                ).build();
        HttpRequest actual = HttpRequest.from("GET", helper.getAuthorizationUrl(
                Credentials.webapp("myClientId", "", "http://www.example.com"), true, true, "scope1", "scope2"
        ));

        URL actualUrl = actual.getUrl();
        URL expectedUrl = expected.getUrl();
        assertEquals(actualUrl.getProtocol(), expectedUrl.getProtocol(), "Scheme was different");
        assertEquals(actualUrl.getHost(), expectedUrl.getHost(), "Host was different");
        assertEquals(actualUrl.getPath(), expectedUrl.getPath(), "Path was different");

        Map<String, String> actualQuery = parseQuery(actual.getUrl().getQuery());
        Map<String, String> expectedQuery = parseQuery(expected.getUrl().getQuery());

        for (Map.Entry<String, String> pair : expectedQuery.entrySet()) {
            if (pair.getKey().equals("state")) {
                // State is random
                continue;
            }
            assertEquals(pair.getValue(), actualQuery.get(pair.getKey()));
        }
    }

    @Test(expectedExceptions = InvalidScopeException.class)
    public void testInvalidScope() throws OAuthException {
        // This class really belongs in a different class but remains here because emulateBrowserAuth is also here
        emulateBrowserAuth(new String[] {"read"});
        reddit.me();
    }

    private Map<String, String> parseQuery(String queryString) {
        Map<String, String> query = new HashMap<>();

        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] parts = pair.split("=");
            query.put(parts[0], parts[1]);
        }

        return query;
    }

    @Test
    public void testLoginApp() throws OAuthException {
        emulateBrowserAuth();

        // Make sure we are authorized
        reddit.me();
    }

    @Test
    public void testRefresh() throws OAuthException {
        emulateBrowserAuth();

        OAuthData before = reddit.getOAuthData();
        reddit.authenticate(reddit.getOAuthHelper().refreshToken(creds));
        OAuthData after = reddit.getOAuthData();
        assertNotEquals(before.getAccessToken(), after.getAccessToken());

        reddit.authenticate(reddit.getOAuthHelper().refreshToken(creds));
        OAuthData secondRefresh = reddit.getOAuthData();
        assertNotEquals(after.getAccessToken(), secondRefresh.getAccessToken());

        // Make sure we are authorized
        reddit.me();
    }

    @Test
    public void testRevokeAccessToken() throws OAuthException {
        emulateBrowserAuth();
        assertTrue(reddit.isAuthenticated());
        reddit.getOAuthHelper().revokeAccessToken(creds);
        assertFalse(reddit.isAuthenticated());
    }

    @Test
    public void testRevokeRefreshToken() throws OAuthException {
        emulateBrowserAuth();
        assertTrue(reddit.isAuthenticated());
        reddit.getOAuthHelper().revokeRefreshToken(creds);
        // Only the refresh token should be revoked, the access token should be fine.
        assertTrue(reddit.isAuthenticated());
        assertFalse(reddit.getOAuthHelper().canRefresh());
    }

    @Test
    public void testApplicationOnly() throws OAuthException {
        reddit.authenticate(reddit.getOAuthHelper().easyAuth(Credentials.userlessApp(creds.getClientId(), UUID.randomUUID())));
        // Try some read-only data
        new SubredditPaginator(reddit).next();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testEasyAuthBadMethod() throws OAuthException {
        reddit.getOAuthHelper().easyAuth(Credentials.webapp("", "", ""));
    }

    private void emulateBrowserAuth() throws OAuthException {
        emulateBrowserAuth(new String[]{"identity"});
    }

    private void emulateBrowserAuth(String[] scopes) throws OAuthException {
        // Emulate a user authenticating this app. If they are not already logged in, they will be prompted to before
        // they're redirected to the page where you can click "allow."
        // Note that this is for testing purposes only and should absolutely not be done in a production app.

        try {
            WebClient client = new WebClient();
            // Reddit does some weird things with JS, but it's not necessary to be emulating them for this test
            client.getOptions().setJavaScriptEnabled(false);

            assertEquals(reddit.getOAuthHelper().getAuthStatus(), OAuthHelper.AuthStatus.NOT_YET);
            URL url = reddit.getOAuthHelper().getAuthorizationUrl(creds, true, scopes);
            assertEquals(reddit.getOAuthHelper().getAuthStatus(), OAuthHelper.AuthStatus.WAITING_FOR_CHALLENGE);

            // Reddit prompts us to log in before we authorize the app
            HtmlPage loginPage = client.getPage(url);
            HtmlForm loginForm = getFirstChild(loginPage.getBody(), "form", "id", "login-form");

            // Change the value of the username and password forms
            Credentials userPassCreds = CredentialsUtils.instance().script();
            // Use the 'script' app credentials because the 'installed' app does not include a username or password
            loginForm.getInputByName("user").setValueAttribute(userPassCreds.getUsername());
            loginForm.getInputByName("passwd").setValueAttribute(userPassCreds.getPassword());

            // Submit the form
            HtmlPage authorizePage = getFirstChild(loginForm, "button", "type", "submit").click();

            // Click the 'Allow' button on the authorize page
            HtmlInput allowInput = getFirstChild(authorizePage.getBody(), "input", "name", "authorize");
            HtmlPage finalPage = allowInput.click();

            // Retrieve the final URL and authorize the app
            URL finalUrl = finalPage.getWebResponse().getWebRequest().getUrl();
            reddit.authenticate(reddit.getOAuthHelper().onUserChallenge(finalUrl.toExternalForm(), creds));

            assertEquals(reddit.getOAuthHelper().getAuthStatus(), OAuthHelper.AuthStatus.AUTHORIZED);
        } catch (IOException e) {
            throw new RuntimeException("Could not login", e);
        }
    }

    private <E extends HtmlElement> E getFirstChild(HtmlElement parent, String elementName, String attrName, String attrVal) {
        List<E> elements = parent.getElementsByAttribute(elementName, attrName, attrVal);
        if (elements.isEmpty()) {
            throw new NoSuchElementException(String.format("Could not find element like <%s %s=%s/>", elementName, attrName, attrVal));
        }
        return elements.get(0);
    }
}
