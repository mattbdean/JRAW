package net.dean.jraw.http;

import com.google.common.net.MediaType;
import com.squareup.okhttp.internal.Util;
import net.dean.jraw.JrawUtils;

import java.util.Map;

/**
 * Utility class to create {@link RequestBody} objects that are {@code x-www-url-form-encoded}. Modeled after OkHttp's
 * {@code FormEncodingBuilder} class.
 */
public final class FormEncodedBodyBuilder {
    private static final MediaType TYPE = MediaTypes.FORM_ENCODED.type();
    private final StringBuilder content;

    /**
     * Creates a RequestBody out of the given map
     */
    public static RequestBody with(Map<String, String> args) {
        FormEncodedBodyBuilder builder = new FormEncodedBodyBuilder();
        for (Map.Entry<String, String> entry : args.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    public FormEncodedBodyBuilder() {
        this.content = new StringBuilder();
    }

    /** Adds a key-value pair */
    public FormEncodedBodyBuilder add(String name, String value) {
        if (content.length() > 0) {
            content.append('&');
        }

        content.append(JrawUtils.urlEncode(name))
                .append('=')
                .append(JrawUtils.urlEncode(value));
        return this;
    }

    /** Creates the RequestBody */
    public RequestBody build() {
        if (content.length() == 0) {
            throw new IllegalStateException("Form encoded body must have at least one part.");
        }

        // Convert to bytes so RequestBody.create() doesn't add a charset to the content-type.
        byte[] contentBytes = content.toString().getBytes(Util.UTF_8);
        return RequestBody.create(TYPE, contentBytes);
    }
}
