package net.dean.jraw.test;

import net.dean.jraw.JrawUtils;
import net.dean.jraw.http.BasicAuthData;
import net.dean.jraw.http.FormEncodedBodyBuilder;
import net.dean.jraw.http.HttpAdapter;
import net.dean.jraw.http.HttpRequest;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.OkHttpAdapter;
import net.dean.jraw.http.RequestBody;
import net.dean.jraw.http.RestResponse;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.CookieManager;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;

public class HttpAdapterTest {
    private final HttpAdapter adapter;
    private static final String HOST = "httpbin.org";

    private static final int TIMEOUT_QUANTITY = 20;
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;
    private static final long EXPECTED_TIMEOUT = 20_000;

    @Factory(dataProvider = "provideAdapters")
    public HttpAdapterTest(HttpAdapter adapter) {
        this.adapter = adapter;
    }

    @Test(dataProvider = "provideHttpMethods")
    public void testMethod(String method, RequestBody body) throws IOException, NetworkException {
        // Send a request to http://httpbind.org/{method} using the given method. If something goes wrong, then httpbin
        // will return a bad status code, else 200
        HttpRequest request = new HttpRequest.Builder()
                .method(method.toUpperCase(), body)
                .host(HOST)
                .path("/{method}", method.toLowerCase())
                .build();
        RestResponse response = adapter.execute(request);
        assertEquals(response.getStatusCode(), 200);
    }

    @Test
    public void testBasicAuth() throws IOException, NetworkException {
        String username = "user123", password = "hunter2";

        adapter.execute(new HttpRequest.Builder()
                .get()
                .https(true)
                .host(HOST)
                .path("/basic-auth/{user}/{password}", username, password)
                .basicAuth(new BasicAuthData(username, password))
                .build());
    }

    @Test
    public void testConnectTimeout() {
        adapter.setConnectTimeout(TIMEOUT_QUANTITY, TIMEOUT_UNIT);
        assertEquals(adapter.getConnectTimeout(), EXPECTED_TIMEOUT);
    }

    @Test
    public void testWriteTimeout() {
        adapter.setWriteTimeout(TIMEOUT_QUANTITY, TIMEOUT_UNIT);
        assertEquals(adapter.getWriteTimeout(), EXPECTED_TIMEOUT);
    }

    @Test
    public void testReadTimeout() {
        adapter.setReadTimeout(TIMEOUT_QUANTITY, TIMEOUT_UNIT);
        assertEquals(adapter.getReadTimeout(), EXPECTED_TIMEOUT);
    }

    @Test
    public void testFollowRedirects() {
        adapter.setFollowRedirects(true);
        assertEquals(adapter.isFollowingRedirects(), true);
    }

    @Test
    public void testProxy() {
        Proxy proxy = Proxy.NO_PROXY;
        adapter.setProxy(proxy);
        assertEquals(adapter.getProxy(), proxy);
    }

    @Test
    public void testCookieManager() {
        CookieManager manager = new CookieManager();
        adapter.setCookieManager(manager);
        assertEquals(adapter.getCookieManager(), manager);
    }

    @DataProvider
    public static Object[][] provideHttpMethods() {
        Map<String, String> args = JrawUtils.mapOf("foo", "bar");
        RequestBody body = FormEncodedBodyBuilder.with(args);
        return new Object[][] {
                {"GET", null},
                {"POST", body},
                {"PATCH", body},
                {"PUT", body},
                {"DELETE", body}
        };
    }

    @DataProvider
    public static Object[][] provideAdapters() {
        HttpAdapter<?>[] adapters = {new OkHttpAdapter()};

        // Create a two-dimensional array where the first layer is adapters.length and the second layer has the same
        // amount of arguments as the HttpAdapterTest constructor
        Object[][] data = new Object[adapters.length][];
        for (int i = 0; i < adapters.length; i++) {
            data[i] = new Object[] {adapters[i]};
        }

        return data;
    }
}
