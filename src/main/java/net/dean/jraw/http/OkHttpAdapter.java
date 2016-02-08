package net.dean.jraw.http;

import okhttp3.*;
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
public final class OkHttpAdapter implements HttpAdapter<OkHttpClient> {
    private static final Protocol DEFAULT_PROTOCOL = Protocol.SPDY_3;
    private static final Protocol FALLBACK_PROTOCOL = Protocol.HTTP_1_1;

    private static OkHttpClient newOkHttpClient() {
        TimeUnit unit = TimeUnit.SECONDS;
        int timeout = 10;
        return new OkHttpClient.Builder()
                .connectTimeout(timeout, unit)
                .readTimeout(timeout, unit)
                .writeTimeout(timeout, unit)
                .build();
    }

    private OkHttpClient http;
    private CookieManager cookieManager;
    private Map<String, String> defaultHeaders;

    public OkHttpAdapter() {
        this(DEFAULT_PROTOCOL);
    }

    public OkHttpAdapter(Protocol protocol) {
        this(newOkHttpClient(), protocol);
    }

    public OkHttpAdapter(OkHttpClient httpClient, Protocol protocol) {
        this.http = httpClient;
        this.cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        this.defaultHeaders = new HashMap<>();

        List<Protocol> protocolList = new ArrayList<>();
        protocolList.add(FALLBACK_PROTOCOL);
        if (FALLBACK_PROTOCOL != protocol) {
            protocolList.add(protocol);
        }
        http = http.newBuilder()
                .protocols(protocolList)
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .build();
    }

    @Override
    public RestResponse execute(HttpRequest request) throws IOException {
        OkHttpClient perRequestClient = http;
        if (request.isUsingBasicAuth()) {
            BasicAuthenticator authenticator = new BasicAuthenticator(request.getBasicAuthData());
            perRequestClient = http.newBuilder().authenticator(authenticator).build();
        }

        Request.Builder builder = new Request.Builder()
                .method(request.getMethod(), request.getBody() == null ? null : new OkHttpRequestBody(request.getBody()))
                .url(request.getUrl())
                .headers(request.getHeaders());

        Response response = perRequestClient.newCall(builder.build()).execute();

        return new RestResponse(request,
                response.body().source().inputStream(),
                response.headers(),
                response.code(),
                response.message(),
                response.protocol().toString().toUpperCase());
    }

    @Override
    public int getConnectTimeout() {
        return http.connectTimeoutMillis();
    }

    @Override
    public void setConnectTimeout(long timeout, TimeUnit unit) {
        http = http.newBuilder().connectTimeout(timeout, unit).build();
    }

    @Override
    public int getReadTimeout() {
        return http.readTimeoutMillis();
    }

    @Override
    public void setReadTimeout(long timeout, TimeUnit unit) {
        http = http.newBuilder().readTimeout(timeout, unit).build();
    }

    @Override
    public int getWriteTimeout() {
        return http.writeTimeoutMillis();
    }

    @Override
    public void setWriteTimeout(long timeout, TimeUnit unit) {
        http = http.newBuilder().writeTimeout(timeout, unit).build();
    }

    @Override
    public boolean isFollowingRedirects() {
        return http.followRedirects();
    }

    @Override
    public void setFollowRedirects(boolean flag) {
        http = http.newBuilder().followRedirects(flag).build();
    }

    @Override
    public Proxy getProxy() {
        return http.proxy();
    }

    @Override
    public void setProxy(Proxy proxy) {
        http = http.newBuilder().proxy(proxy).build();
    }

   @Override
    public CookieManager getCookieManager() {
        return cookieManager;
    }

    @Override
    public void setCookieManager(CookieManager manager) {
        this.cookieManager = manager;
        http = http.newBuilder().cookieJar(new JavaNetCookieJar(cookieManager)).build();
    }

    @Override
    public Map<String, String> getDefaultHeaders() {
        return defaultHeaders;
    }

    @Override
    public OkHttpClient getNativeClient() {
        return http;
    }

    /** Mirrors a JRAW RequestBody to an OkHttp RequestBody */
    private static class OkHttpRequestBody extends okhttp3.RequestBody {
        private RequestBody mirror;
        private MediaType contentType = null; // Lazily initialized

        public OkHttpRequestBody(RequestBody mirror) {
            this.mirror = mirror;
        }

        @Override
        public MediaType contentType() {
            if (mirror.contentType() == null)
                return null;
            if (contentType != null)
                return contentType;
            contentType = MediaType.parse(mirror.contentType().toString());
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
        public Request authenticate(Route route, Response response) throws IOException {
            String credential = Credentials.basic(data.getUsername(), data.getPassword());
            String header = response.code() == 407 ? "Proxy-Authorization" : "Authorization";
            return response.request().newBuilder().header(header, credential).build();
        }
    }
}
