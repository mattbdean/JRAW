package net.dean.jraw.http;

public class RestRequest extends HttpRequest {
    private final ContentType expected;

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

    public ContentType getExpected() {
        return expected;
    }

    public static class Builder extends HttpRequest.Builder<RestRequest, Builder> {
        private ContentType expected;

        public Builder(HttpVerb verb, String hostname, String path) {
            super(verb, hostname, path);
            // Defaults to JSON
            this.expected = ContentType.JSON;
        }

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
