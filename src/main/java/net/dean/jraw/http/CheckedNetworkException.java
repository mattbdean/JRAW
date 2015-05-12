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
        super(getExceptionMessage(response));
        this.response = response;
    }

    private static String getExceptionMessage(RestResponse response) {
        if (response == null)
            throw new NullPointerException("response cannot be null");

        return String.format("Request returned non-successful status code: %s %s",
                response.getStatusCode(),
                response.getStatusMessage());
    }

    public RestResponse getResponse() {
        return response;
    }
}
