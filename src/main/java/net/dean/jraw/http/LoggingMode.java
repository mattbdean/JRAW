package net.dean.jraw.http;

public enum LoggingMode {
    /** Always log HTTP requests and responses. Time sleeping will also be shown. */
    ALWAYS,
    /** Never log HTTP requests and responses */
    NEVER,
    /**
     * Log HTTP requests and responses if and only if the request is not successful, which means it has a status code of
     * [200..300).
     */
    ON_FAIL
}
