package net.dean.jraw.http;

import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.http.HttpMethod;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static net.dean.jraw.http.HttpLogger.Component.*;

/**
 * This class is responsible for logging HTTP requests and responses, particularly the {@link RestRequest} and
 * {@link RestResponse} classes. The parts of the request and response are broken into parts called {@link Component}s.
 * By default, all of these components are enabled (except for {@link Component#RESPONSE_BODY_ALWAYS_FULL}. To enable or
 * disable a Component, you can use {@link #enable(Component)} or {@link #disable(Component)} respectively.
 */
public class HttpLogger {
    private static final String INDENT = "    ";
    private static final String ELLIPSIS = "...";
    private static final int RESPONSE_BODY_CUTOFF = 100 - ELLIPSIS.length();
    private final Logger l;
    private Map<Component, Boolean> components;

    /**
     * Instantiates a new HttpLogger
     * @param logger The SLF4J logger to use
     */
    public HttpLogger(Logger logger) {
        this.l = logger;
        this.components = new HashMap<>();
        for (Component c : Component.values()) {
            components.put(c, true);
        }
        disable(RESPONSE_BODY_ALWAYS_FULL); // Short response bodies by default
    }

    /**
     * Checks if a specific Component is enabled
     * @param c The component to check
     * @return True, if the component is being logged, false if else
     */
    public boolean isEnabled(Component c) {
        return components.get(c);
    }

    /**
     * Enables a given Component
     * @param c The component to enable
     */
    public void enable(Component c) {
        components.put(c, true);
    }

    /**
     * Disables a given component
     * @param c The component to disable
     */
    public void disable(Component c) {
        components.put(c, false);
    }

    private String getIndent(String header) {
        char separator = ' ';
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < header.length(); i++) {
            sb.append(separator);
        }

        return sb.toString();
    }

    private boolean contains(String str, String[] arr) {
        if (arr == null || arr.length == 0) return false;
        for (String str2 : arr) {
            if (str.equals(str2)) {
                return true;
            }
        }

        return false;
    }

    private void logMap(String header, Map<String, String> data, String[] sensitiveKeys) {
        header = INDENT + header + ": {";

        if (data == null || data.size() == 0) {
            l.info("{}}", header); // my-data: {}
            return;
        }

        String indent = getIndent(header);
        int counter = 0;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            l.info("{}{}={}{}", counter != 0 ? indent : header,
                    entry.getKey(),
                    contains(entry.getKey(), sensitiveKeys) ? "<sensitive>" : entry.getValue(),
                    counter == data.size() - 1 ? '}' : ',');
            counter++;
        }
    }

    /**
     * Logs at INFO if the response was successful, otherwise at ERROR.
     * @param r The response to check the success of
     * @param format Passed to {@link Logger#info(String, Object...)} or its {@code error()} sibling
     * @param params Passed to {@link Logger#info(String, Object...)} or its {@code error()} sibling
     */
    private void logBySuccess(Response r, String format, Object... params) {
        if (r.isSuccessful())
            l.info(format, params);
        else
            l.error(format, params);
    }

    /**
     * Logs an HTTP request in this format:
     *
     * <pre>{@code
     * $requestDescriptor
     *     form-data: {$key1=$val2,
     *                 $key2=$val2}
     *     headers: {$key1=$val1}
     * }</pre>
     *
     * Where {@code $requestDescriptor} is the combination of the HTTP method and URL (ex: "POST http://www.example.com").
     * @param r The request to log
     */
    public void log(RestRequest r) {
        if (isEnabled(REQUEST)) {
            if (isEnabled(REQUEST_DESCRIPTOR)) {
                l.info("{} {}", r.getMethod(), r.getUrl());
            }
            if (isEnabled(REQUEST_FORM_DATA) && HttpMethod.hasRequestBody(r.getMethod())) {
                logMap("form-data", r.getFormArgs(), r.getSensitiveArgs());
            }
            if (isEnabled(REQUEST_HEADERS)) {
                // Create a map out of the request's Headers
                Map<String, String> map = new HashMap<>();
                for (String key : r.getOkHttpRequest().headers().names()) {
                    map.put(key, r.getOkHttpRequest().headers().get(key));
                }

                logMap("headers", map, null);
            }
        }
    }

    /**
     * Logs an HTTP response in this format:
     *
     * <pre>{@code
     * $protocol $code $message
     *     headers: {$key1=$val1,
     *               $key2=$val2}
     * }</pre>
     *
     * @param r The response to log
     */
    public void log(RestResponse r) {
        Response okResponse = r.getOkHttpResponse();
        if (isEnabled(RESPONSE)) {
            logBySuccess(okResponse, "{} {} {}", okResponse.protocol().toString().toUpperCase(), okResponse.code(), okResponse.message());
            if (isEnabled(RESPONSE_CONTENT_TYPE)) {
                logBySuccess(okResponse, "{}content-type: {}", INDENT, okResponse.header("Content-Type", "(unknown)"));
            }
            if (isEnabled(RESPONSE_BODY)) {
                String raw = r.getRaw();

                if (!isEnabled(RESPONSE_BODY_ALWAYS_FULL) && okResponse.isSuccessful()) {
                    // If the request was successful the response isn't as important.
                    // Display the full response if the request was not successful
                    raw = raw.replace("\n", "").replace("\r", "").replace("\t", "");
                    if (raw.length() >= RESPONSE_BODY_CUTOFF) {
                        raw = raw.substring(0, RESPONSE_BODY_CUTOFF);
                        raw += ELLIPSIS;
                    }
                }
                if (raw.isEmpty()) {
                    raw = "<nothing>";
                }
                logBySuccess(okResponse, "{}response-body: {}", INDENT, raw);
            }
        }
    }

    /**
     * Represents the components involved in logging an HTTP request and response
     */
    public static enum Component {
        /** The entire request */
        REQUEST,
        /** The request descriptor (ex: "POST https://www.example.com" */
        REQUEST_DESCRIPTOR,
        /** Form data (used in POST, PUT, etc.) */
        REQUEST_FORM_DATA,
        /** Headers sent to the server */
        REQUEST_HEADERS,

        /** The entire response */
        RESPONSE,
        /** The 'Content-Type' header */
        RESPONSE_CONTENT_TYPE,
        /**
         * The raw response data. Newlines will be removed unless either {@link #RESPONSE_BODY_ALWAYS_FULL} is enabled
         * or the response was not successful.
         */
        RESPONSE_BODY,
        /** Whether or not to always log the full response body, regardless of the request's success */
        RESPONSE_BODY_ALWAYS_FULL
    }
}
