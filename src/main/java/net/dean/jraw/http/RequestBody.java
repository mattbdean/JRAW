package net.dean.jraw.http;

import com.google.common.net.MediaType;
import com.squareup.okhttp.internal.Util;
import okio.BufferedSink;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Modeled after OkHttp's RequestBody class, but uses a Guava MediaType
 */
public abstract class RequestBody {
    public abstract MediaType contentType();

    public long contentLength() { return -1; }

    public abstract void writeTo(BufferedSink sink) throws IOException;

    public static RequestBody create(MediaType contentType, String content) {
        Charset charset = Util.UTF_8;
        if (contentType != null) {
            charset = contentType.charset().orNull();
            if (charset == null) {
                charset = Util.UTF_8;
                contentType = MediaType.parse(contentType + "; charset=utf-8");
            }
        }
        byte[] bytes = content.getBytes(charset);
        return create(contentType, bytes);
    }

    /** Returns a new request body that transmits {@code content}. */
    public static RequestBody create(final MediaType contentType, final byte[] content) {
        if (content == null) throw new NullPointerException("content is null");
        return new DefaultRequestBody(contentType, content);
    }

    static final class DefaultRequestBody extends RequestBody {
        private final MediaType type;
        private final byte[] content;

        public DefaultRequestBody(MediaType contentType, byte[] content) {
            this.type = contentType;
            this.content = content;
        }

        @Override
        public MediaType contentType() {
            return type;
        }

        @Override
        public long contentLength() {
            return content.length;
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            sink.write(content);
        }
    }
}
