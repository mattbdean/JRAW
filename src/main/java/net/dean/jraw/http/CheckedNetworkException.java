package net.dean.jraw.http;

/**
 * A checked version of NetworkException
 */
public class CheckedNetworkException extends Exception {
    private final RestResponse response;

    public CheckedNetworkException(NetworkException ex) {
        this(ex.getResponse());
    }

    public CheckedNetworkException(RestResponse response) {
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
