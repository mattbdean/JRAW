package net.dean.jraw.http;

/**
 * This class provides the ability to provide an expected Content-Type value
 */
public class RestRequest extends HttpRequest {
    private final ContentType expected;

    /**
     * Instantiates a new RestRequest
     * @param b The Builder to use
     */
    public RestRequest(Builder b) {
        super(b);
        this.expected = b.expected;
    }

    /**
     * Instantiates a simple RestRequest
     *
     * @param verb     The HTTP verb to use
     * @param hostname The host to use
     * @param path     The path of the request. For example, "/api/login".
     */
    public RestRequest(HttpVerb verb, String hostname, String path) {
        this(new Builder(verb, hostname, path).expectedContentType(ContentType.JSON));
    }

    /**
     * Gets the expected ContentType
     * @return The expected ContentType
     */
    public ContentType getExpected() {
        return expected;
    }

    /**
     * This class provides the ability to create RestRequests using the builder pattern
     */
    public static class Builder extends HttpRequest.Builder<RestRequest, Builder> {
        private ContentType expected;

        /**
         * Instantiates a new Builder
         * @param verb The HTTP verb to use
         * @param hostname The host to use
         * @param path The path of the request. For example, "/api/login".
         */
        public Builder(HttpVerb verb, String hostname, String path) {
            super(verb, hostname, path);
            // Defaults to JSON
            this.expected = ContentType.JSON;
        }

        /**
         * Sets the expected ContentType for this request
         * @param expected The expected ContentType
         * @return This Builder
         */
        public Builder expectedContentType(ContentType expected) {
            this.expected = expected;
            return this;
        }

        @Override
        public RestRequest build() {
            return new RestRequest(this);
        }
    }
}
