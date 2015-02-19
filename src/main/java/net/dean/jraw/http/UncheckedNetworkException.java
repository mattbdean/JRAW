package net.dean.jraw.http;

/**
 * The same as {@link NetworkException}, except extending RuntimeException instead of Exception.
 */
public class UncheckedNetworkException extends RuntimeException {
    private final RestResponse response;

    public UncheckedNetworkException(RestResponse response) {
        super(String.format("Request returned non-successful status code: %s %s",
                response.getStatusCode(),
                response.getStatusMessage()));
        if (response == null)
            throw new NullPointerException("response cannot be null");
        this.response = response;
    }

    public RestResponse getResponse() {
        return response;
    }
}
