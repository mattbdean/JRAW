package net.dean.jraw.http;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import okio.BufferedSink;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Provides a concrete HttpAdapter implementation using Square's OkHttp
 */
public final class OkHttpAdapter implements HttpAdapter {
    private static final Protocol DEFAULT_PROTOCOL = Protocol.SPDY_3;
    private static final Protocol FALLBACK_PROTOCOL = Protocol.HTTP_1_1;
    private OkHttpClient http;
    private CookieManager cookieManager;
    private Map<String, String> defaultHeaders;

    public OkHttpAdapter() {
        this(DEFAULT_PROTOCOL);
    }

    public OkHttpAdapter(Protocol protocol) {
        this.http = new OkHttpClient();
        this.cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        this.defaultHeaders = new HashMap<>();

        List<Protocol> protocolList = new ArrayList<>();
        protocolList.add(FALLBACK_PROTOCOL);
        if (FALLBACK_PROTOCOL != protocol) {
            protocolList.add(protocol);
        }
        http.setProtocols(protocolList);
        http.setCookieHandler(cookieManager);
    }

    @Override
    public RestResponse execute(HttpRequest request) throws IOException {
        if (request.isUsingBasicAuth()) {
            http.setAuthenticator(new BasicAuthenticator(request.getBasicAuthData()));
        }

        try {
            Request.Builder builder = new Request.Builder()
                    .method(request.getMethod(), request.getBody() == null ? null : new OkHttpRequestBody(request.getBody()))
                    .url(request.getUrl())
                    .headers(request.getHeaders());

            Response response = http.newCall(builder.build()).execute();

            return new RestResponse(request,
                    response.body().source().inputStream(),
                    response.headers(),
                    response.code(),
                    response.message(),
                    response.protocol().toString().toUpperCase());
        } finally {
            // Recover by removing the BasicAuthenticator
            http.setAuthenticator(null);
        }
    }

    @Override
    public int getConnectTimeout() {
        return http.getConnectTimeout();
    }

    @Override
    public void setConnectTimeout(long timeout, TimeUnit unit) {
        http.setConnectTimeout(timeout, unit);
    }

    @Override
    public int getReadTimeout() {
        return http.getReadTimeout();
    }

    @Override
    public void setReadTimeout(long timeout, TimeUnit unit) {
        http.setReadTimeout(timeout, unit);
    }

    @Override
    public int getWriteTimeout() {
        return http.getWriteTimeout();
    }

    @Override
    public void setWriteTimeout(long timeout, TimeUnit unit) {
        http.setWriteTimeout(timeout, unit);
    }

    @Override
    public boolean isFollowingRedirects() {
        return http.getFollowRedirects();
    }

    @Override
    public void setFollowRedirects(boolean flag) {
        http.setFollowRedirects(flag);
    }

    @Override
    public Proxy getProxy() {
        return http.getProxy();
    }

    @Override
    public void setProxy(Proxy proxy) {
        http.setProxy(proxy);
    }

    @Override
    public CookieManager getCookieManager() {
        return cookieManager;
    }

    @Override
    public void setCookieManager(CookieManager manager) {
        this.cookieManager = manager;
        http.setCookieHandler(cookieManager);
    }

    @Override
    public Map<String, String> getDefaultHeaders() {
        return defaultHeaders;
    }

    /** Mirrors a JRAW RequestBody to an OkHttp RequestBody */
    private static class OkHttpRequestBody extends com.squareup.okhttp.RequestBody {
        private RequestBody mirror;
        private com.squareup.okhttp.MediaType contentType = null; // Lazily initialized

        public OkHttpRequestBody(RequestBody mirror) {
            this.mirror = mirror;
        }

        @Override
        public com.squareup.okhttp.MediaType contentType() {
            if (mirror.contentType() == null)
                return null;
            if (contentType != null)
                return contentType;
            contentType = com.squareup.okhttp.MediaType.parse(mirror.contentType().toString());
            return contentType;
        }

        @Override
        public long contentLength() {
            return mirror.contentLength();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            mirror.writeTo(sink);
        }
    }

    /**
     * This class is responsible for doing basic HTTP authentication. See
     * <a href="http://tools.ietf.org/html/rfc2617">RFC 2617</a> for more details.
     */
    private static class BasicAuthenticator implements Authenticator {
        private final BasicAuthData data;

        public BasicAuthenticator(BasicAuthData data) {
            this.data = data;
        }

        @Override
        public Request authenticate(Proxy proxy, Response response) throws IOException {
            String credential = com.squareup.okhttp.Credentials.basic(data.getUsername(), data.getPassword());
            return response.request().newBuilder().header("Authorization", credential).build();
        }

        @Override
        public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
            String credential = com.squareup.okhttp.Credentials.basic(data.getUsername(), data.getPassword());
            return response.request().newBuilder().header("Proxy-Authorization", credential).build();
        }
    }
}
