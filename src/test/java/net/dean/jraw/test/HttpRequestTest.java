package net.dean.jraw.test;

import net.dean.jraw.Endpoints;
import net.dean.jraw.http.HttpRequest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class HttpRequestTest {
    @Test
    public void testBasic() {
        compare("https://example.com/hello", new HttpRequest.Builder()
                .get()
                .https(true)
                .host("example.com")
                .path("/hello")
                .build());
    }

    @Test
    public void testPathParams() {
        compare("http://example.com/id/foo", new HttpRequest.Builder()
                .get()
                .https(false)
                .host("example.com")
                .path("/id/{id}", "foo")
                .build());
    }

    @Test
    public void testEndpointPathParams() {
        compare("https://example.com/api/multi/foo/r/bar", new HttpRequest.Builder()
                .get()
                .https(true)
                .host("example.com")
                .endpoint(Endpoints.MULTI_MULTIPATH_R_SRNAME_DELETE, "foo", "bar")
                .build());
    }

    private void compare(String expectedUrl, HttpRequest actual) {
        assertEquals(actual.getUrl().toExternalForm(), expectedUrl);
    }
}
