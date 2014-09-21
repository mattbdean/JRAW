package net.dean.jraw.http;

import net.dean.jraw.JrawUtils;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * This class provides a way to consolidate all the main attributes of a RESTful HTTP request into one object
 */
public class HttpRequest {
    /** The HTTP verb to use to execute the request */
    private final HttpVerb verb;
    /** The path relative to the root of the host */
    private final String path;
    /** The arguments to be passed either by query string if the method is GET or DELETE, or by form if it is a different request */
    private final Map<String, String> args;
    private final String hostname;
    /** The time this request was executed */
    private LocalDateTime executed;
    private final String cookieSpec;

    /**
     * Instantiates a simple RestRequest
     *
     * @param verb The HTTP verb to use
     * @param hostname The host to use. For example, "reddit.com" or "ssl.reddit.com"
     * @param path The path of the request. For example, "/api/login".
     */
    public HttpRequest(HttpVerb verb, String hostname, String path) {
        this(new Builder(verb, hostname, path));
    }

    /**
     * Instantiates a new HttpRequest
     * @param b The Builder to use
     */
    protected HttpRequest(Builder b) {
        this.verb = b.verb;
        this.hostname = b.hostname;
        this.path = b.path;
        this.args = b.args;
        this.cookieSpec = b.cookieSpec;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    /**
     * Get the time that this request was executed. Mainly used for ratelimiting.
     *
     * @return The time this request was executed, or null if it hasn't been executed yet.
     * @see net.dean.jraw.RedditClient#setRequestManagementEnabled(boolean)
     */
    public LocalDateTime getExecuted() {
        return executed;
    }

    public HttpVerb getVerb() {
        return verb;
    }

    public String getHostname() {
        return hostname;
    }

    /**
     * Called when this request is executed to take note of the current time.
     * @throws IllegalStateException If this method has been called more than once
     */
    public void onExecuted() {
        if (executed != null) {
            throw new IllegalStateException("Already executed (" + executed + ")");
        }
        this.executed = LocalDateTime.now();
    }

    /**
     * Gets the cookie spec used for executing this request. Only really used for requests that set a "secure_session"
     * cookie (aka {@link net.dean.jraw.RedditClient#login(String, String)}).
     * @return The cookie spec this request will use
     */
    public String getCookieSpec() {
        return cookieSpec;
    }

    @Override
    public String toString() {
        return "HttpRequest {" +
                "verb=" + verb +
                ", path='" + path + '\'' +
                ", args=" + args +
                ", hostname='" + hostname + '\'' +
                ", executed=" + executed +
                ", cookieSpec='" + cookieSpec + '\'' +
                '}';
    }

    /**
     * This class is responsible for building HttpRequest objects. When extending this class where {@literal <T>} is not
     * {@link HttpRequest}, you <b>must</b> override {@link #build()} lest your application will become flooded with
     * ClassCastExceptions.
     *
     * @param <T> The type of HttpRequest to return
     * @param <U> The type of Builder to return in {@link #build()}
     */
    public static class Builder<T extends HttpRequest, U extends Builder<T, U>> {
        protected final HttpVerb verb;
        protected final String hostname;
        protected final String path;
        protected Map<String, String> args;
        protected String cookieSpec;

        /**
         * Instantiates a new Builder
         * @param verb The HTTP verb to use
         * @param hostname The host to use. For example, "reddit.com" or "ssl.reddit.com"
         * @param path The path of the request. For example, "/api/login".
         */
        public Builder(HttpVerb verb, String hostname, String path) {
            this.verb = verb;
            this.hostname = hostname;
            this.path = path;
            this.cookieSpec = HttpHelper.COOKIE_SPEC_DEFAULT;
        }

        /**
         * Sets the query args for GET and DELETE requests or the form args for other HTTP verbs.
         * @param args The arguments to use
         * @return This Builder
         */
        @SuppressWarnings("unchecked")
        public U args(Map<String, String> args) {
            this.args = args;
            return (U) this;
        }

        /**
         * Sets the query args for GET and DELETE requests or the form args for other HTTP verbs. The Object array must
         * meet the requirements described in {@link net.dean.jraw.JrawUtils#args(Object...)} for this method to complete
         * successfully.
         * @param args The arguments to use
         * @return This Builder
         */
        @SuppressWarnings("unchecked")
        public U args(Object... args) {
            this.args = JrawUtils.args(args);
            return (U) this;
        }

        /**
         * Sets the cookie spec used for executing this request. Only really used for requests that set a "secure_session"
         * cookie (aka {@link net.dean.jraw.RedditClient#login(String, String)}).
         *
         * @param cookieSpec The cookie spec to use. Valid values are constants in  the
         *                   {@link org.apache.http.client.config.CookieSpecs} class and {@code COOKIE_SPEC_*} constants
         *                   in {@link net.dean.jraw.http.HttpHelper}.
         * @return This Builder
         */
        public Builder cookieSpec(String cookieSpec) {
            this.cookieSpec = cookieSpec;
            return this;
        }

        /**
         * Instantiates a new HttpRequest or one of its subclasses
         * @return A new HttpRequest
         */
        public T build() {
            return (T) new HttpRequest(this);
        }
    }
}
