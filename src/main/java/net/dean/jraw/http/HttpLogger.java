package net.dean.jraw.http;

import com.squareup.okhttp.Headers;
import net.dean.jraw.JrawUtils;
import okio.Buffer;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static net.dean.jraw.http.HttpLogger.Component.*;

/**
 * This class is responsible for logging objects relating to HTTP network activity, particularly the {@link HttpRequest}
 * and {@link RestResponse} classes. The parts of the request and response are broken into parts called
 * {@link Component components}. By default, all of these components are enabled (except for
 * {@link Component#RESPONSE_BODY_ALWAYS_FULL}. To enable or disable a Component, you can use
 * {@link #enable(Component)} or {@link #disable(Component)} respectively.
 */
public class HttpLogger {
    /** What will replace the latter part of the response body if it needs to be trimmed. */
    public static final String ELLIPSIS = "...";
    private static final String INDENT = "    ";
    private static final String CENSOR = "<sensitive>";
    private static final int RESPONSE_BODY_CUTOFF = 100 - ELLIPSIS.length();
    private final Logger l;
    private Map<Component, Boolean> components;

    /**
     * Instantiates a new HttpLogger
     * @param logger The SLF4J logger to use
     */
    public HttpLogger(Logger logger) {
        this.l = logger;
        this.components = new EnumMap<>(Component.class);
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

    private String formatHeader(String header) {
        return INDENT + header + ": {";
    }

    private void logHeaders(boolean successful, Headers h) {
        Map<String, String> map = new HashMap<>();
        for (String key : h.names()) {
            map.put(key, h.get(key));
        }
        logMap(successful, "headers", map, null, ": ");
    }

    private void logMap(boolean successful, String header, Map<String, String> data, String[] sensitiveKeys) {
        logMap(successful, header, data, sensitiveKeys, "=");
    }

    private void logMap(boolean successful, String header, Map<String, String> data, String[] sensitiveKeys, String separator) {
        if (isEnabled(ALPHABETIZE_MAPS)) {
            Map<String, String> unsorted = data;
            data = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            data.putAll(unsorted);
        }
        header = formatHeader(header);

        if (data == null || data.size() == 0) {
            logBySuccess(successful, "{}}", header); // my-data: {}
            return;
        }

        String indent = getIndent(header);
        int counter = 0;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            logBySuccess(successful, "{}{}{}{}{}",
                    counter != 0 ? indent : header, // If the first one, output the header, otherwise indent
                    JrawUtils.urlDecode(entry.getKey()),
                    separator,
                    // Censor the value if need be
                    contains(entry.getKey(), sensitiveKeys) ? CENSOR : JrawUtils.urlDecode(entry.getValue()),
                    counter == data.size() - 1 ? '}' : ','); // Use a comma if there are more, else a closing bracket
            counter++;
        }
    }

    private void logBody(String header, String data) {
        header = formatHeader(header);
        l.info("{} {}}", header, data);
    }

    /**
     * Logs at INFO if the response was successful, otherwise at ERROR.
     * @param r The response to check the success of
     * @param format Passed to {@link Logger#info(String, Object...)} or its {@code error()} sibling
     * @param params Passed to {@link Logger#info(String, Object...)} or its {@code error()} sibling
     */
    private void logBySuccess(RestResponse r, String format, Object... params) {
        logBySuccess(r.isSuccessful(), format, params);
    }

    /**
     * Logs at INFO {@code successful}, otherwise at ERROR.
     * @param format Passed to {@link Logger#info(String, Object...)} or its {@code error()} sibling
     * @param params Passed to {@link Logger#info(String, Object...)} or its {@code error()} sibling
     */
    private void logBySuccess(boolean successful, String format, Object... params) {
        if (successful)
            l.info(format, params);
        else
            l.error(format, params);

    }

    private String getContent(RequestBody body) {
        Buffer buff = new Buffer();
        try {
            body.writeTo(buff);
        } catch (IOException e) {
            throw new RuntimeException("Could not write the body", e);
        }

        return buff.readUtf8();
    }

    private Map<String, String> parseUrlEncoded(RequestBody requestBody) {
        return JrawUtils.parseUrlEncoded(getContent(requestBody));
    }

    /**
     * Logs an HTTP request in this format:
     *
     * <pre>{@code
     * $method $url
     *     form-data: {$key1=$val2,
     *                 $key2=$val2}
     *     headers: {$key1: $val1}
     * }</pre>
     *
     * Where {@code $requestDescriptor} is the combination of the HTTP method and URL (ex: "POST http://www.example.com").
     * @param r The request to log
     */
    public void log(HttpRequest r) {
        log(r, true);
    }

    /**
     * Logs an HTTP request in this format:
     *
     * <pre>{@code
     * $method $url
     *     form-data: {$key1=$val2,
     *                 $key2=$val2}
     *     headers: {$key1: $val1}
     * }</pre>
     *
     * Where {@code $requestDescriptor} is the combination of the HTTP method and URL (ex: "POST http://www.example.com").
     * @param r The request to log
     * @param wasSuccessful If true, then the message will be logged at INFO, otherwise ERROR.
     */
    public void log(HttpRequest r, boolean wasSuccessful) {
        if (isEnabled(REQUEST)) {
            if (isEnabled(REQUEST_DESCRIPTOR)) {
                logBySuccess(wasSuccessful, "{} {}", r.getMethod(), r.getUrl());
            }
            if (isEnabled(REQUEST_BODY) && r.getBody() != null) {
                if (isEnabled(REQUEST_FORMAT_FORM) &&
                        r.getBody().contentType() != null && // Will be null if no body was sent
                        JrawUtils.isEqual(r.getBody().contentType(), MediaTypes.FORM_ENCODED.type())) {
                    // Body Content-Type was x-www-form-urlencoded
                    logMap(true, "form-data", parseUrlEncoded(r.getBody()), r.getSensitiveArgs());
                } else {
                    // Other Content-Type
                    logBody("body", getContent(r.getBody()));
                }
            }
            if (isEnabled(REQUEST_HEADERS)) {
                // Create a map out of the request's Headers
                logHeaders(wasSuccessful, r.getHeaders());
            }
            if (isEnabled(REQUEST_BASIC_AUTH) && r.isUsingBasicAuth()) {
                Map<String, String> data = new HashMap<>();
                data.put("username", r.getBasicAuthData().getUsername());
                data.put("password", CENSOR); // Don't even make 'password' a sensitive arg, just go right for it
                logMap(wasSuccessful, "basic-auth", data, new String[0]);
            }
        }
    }

    /**
     * Logs an HTTP response in this format:
     *
     * <pre>{@code
     * $protocol $code $message
     *     headers: {$key1: $val1,
     *               $key2: $val2}
     * }</pre>
     *
     * @param r The response to log
     */
    public void log(RestResponse r) {
        if (isEnabled(RESPONSE)) {
            logBySuccess(r, "{} {} {}", r.getProtocol(), r.getStatusCode(), r.getStatusMessage());
            if (isEnabled(RESPONSE_HEADERS)) {
                logHeaders(r.isSuccessful(), r.getHeaders());
            }
            if (isEnabled(RESPONSE_BODY)) {
                String raw = r.getRaw();

                if (!isEnabled(RESPONSE_BODY_ALWAYS_FULL) && r.isSuccessful()) {
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
                logBySuccess(r, "{}response-body: {}", INDENT, raw);
            }
        }
    }

    /**
     * Represents the components involved in logging an HTTP request and response
     */
    public static enum Component {
        /** The entire request */
        REQUEST,
        /** The HTTP verb and URL. For example, "{@code POST https://www.example.com}". Depends on {@link #REQUEST} */
        REQUEST_DESCRIPTOR,
        /** The entirety of the request body. Depends on {@link #REQUEST}*/
        REQUEST_BODY,
        /**
         * Format and decode the response body to be more readable. Only applies if the request Content-Type was
         * {@code application/x-www-form-urlencoded}. Depends on {@link #REQUEST_BODY}
         */
        REQUEST_FORMAT_FORM,
        /** Headers sent to the server. Depends on {@link #REQUEST} */
        REQUEST_HEADERS,
        /** Basic Auth data, if applicable. Password will be censored. Depends on {@link #REQUEST}. */
        REQUEST_BASIC_AUTH,


        /** The entire response */
        RESPONSE,
        /** Response's headers. Depends on {@link #RESPONSE}. */
        RESPONSE_HEADERS,
        /**
         * The raw response data. Newlines will be removed unless either {@link #RESPONSE_BODY_ALWAYS_FULL} is enabled
         * or the response was not successful. Depends on {@link #RESPONSE}.
         */
        RESPONSE_BODY,
        /**
         * Whether or not to always log the full response body, regardless of the request's success. If this component
         * is disabled, the body will be trimmed and an ellipsis will be appended.
         *
         * @see #ELLIPSIS
         */
        RESPONSE_BODY_ALWAYS_FULL,

        /**
         * Will copy any data with keys and values (headers, form data, etc.) to a case-insensitive TreeMap before
         * iterating and logging. Applies to any data, whether that be in the request or response.
         */
        ALPHABETIZE_MAPS
    }
}
