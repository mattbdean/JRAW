package net.dean.jraw.http;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.Proxy;

/**
 * This class is responsible for doing basic HTTP authentication. See
 * <a href="http://tools.ietf.org/html/rfc2617">RFC 2617</a> for more details.
 */
public class BasicAuthenticator implements Authenticator {
    private final String username;
    private final String password;

    public BasicAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Request authenticate(Proxy proxy, Response response) throws IOException {
        String credential = Credentials.basic(username, password);
        return response.request().newBuilder().header("Authorization", credential).build();
    }

    @Override
    public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
        String credential = Credentials.basic(username, password);
        return response.request().newBuilder().header("Proxy-Authorization", credential).build();
    }
}
