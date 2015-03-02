package net.dean.jraw.models;

import net.dean.jraw.models.meta.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.URL;

/**
 * Represents media provided by an oEmbed-style JSON object. Documentation in this class is borrowed heavily from the
 * <a href="http://oembed.com/#section2.3">oEmbed website</a>.
 */
public class OEmbed extends JsonModel {

    /** Instantiates a new OEmbed object */
    public OEmbed(JsonNode oembedNode) {
        super(oembedNode);
    }

    /** The resource's MIME type. */
    @JsonProperty
    public MediaType getMediaType() {
        String typeString = data("type");

        for (MediaType t : MediaType.values()) {
            if (t.name().equalsIgnoreCase(typeString)) {
                return t;
            }
        }

        return null;
    }

    /** Gets a text title that describes the resource */
    @JsonProperty(nullable = true)
    public String getTitle() {
        return data("title");
    }

    /** Gets the oEmbed version number. This must be 1.0. */
    @JsonProperty(nullable = true)
    public String getVersion() {
        return data("version");
    }

    /** Gets the name of the author/owner of the resource. */
    @JsonProperty(nullable = true)
    public String getAuthorName() {
        return data("author_name");
    }

    /** Gets a URL for the author/owner of the resource. */
    @JsonProperty(nullable = true)
    public String getAuthorUrl() {
        return data("author_url");
    }

    /** Gets the name of the resource provider. */
    @JsonProperty(nullable = true)
    public String getProviderName() {
        return data("provider_name");
    }

    /** Gets the URL of the resource provider. */
    @JsonProperty(nullable = true)
    public String getProviderUrl() {
        return data("provider_url");
    }

    /**
     * The <em>suggested</em> cache lifetime for this resource, in seconds. Consumers may choose to use this value or
     * not.
     */
    @JsonProperty(nullable = true)
    public Integer getCacheAge() {
        return data("cache_age", Integer.class);
    }

    /** Gets the thumbnail associated with this resource. Can be null. */
    @JsonProperty(nullable = true)
    public Thumbnail getThumbnail() {
        if (!data.has("thumbnail_url")) {
            return null;
        }
        return new Thumbnail(data("thumbnail_url", URL.class),
                data("thumbnail_width", Integer.class),
                data("thumbnail_height", Integer.class));
    }

    /**
     * Gets the width in pixels required to display the video, image, or HTML. Will be present when the media type is
     * not {@link MediaType#LINK}.
     */
    @JsonProperty(nullable = true)
    public Integer getWidth() {
        return data("width", Integer.class);
    }

    /**
     * Gets the height in pixels required to display the video, image, or HTML. Will be present when the media type is
     * not {@link MediaType#LINK}.
     */
    @JsonProperty(nullable = true)
    public Integer getHeight() {
        return data("height", Integer.class);
    }

    /**
     * Gets the source URL of the image. Present when the media type is {@link MediaType#PHOTO}.
     */
    @JsonProperty(nullable = true)
    public String getUrl() {
        return data("url");
    }

    /**
     * Gets the HTML required to embed a video player (if video) or display the resource (if rich). Present when the
     * media type is {@link MediaType#VIDEO} or {@link MediaType#RICH}.
     */
    @JsonProperty(nullable = true)
    public String getHtml() {
        return data("html");
    }

    /**
     * Represents an oEmbed thumbnail
     */
    public static class Thumbnail {
        private final URL url;
        private final int width;
        private final int height;

        private Thumbnail(URL url, int width, int height) {
            this.url = url;
            this.width = width;
            this.height = height;
        }

        /** Gets the URL to a thumbnail image representing the resource */
        public URL getUrl() {
            return url;
        }

        /** Gets the width of the thumbnail. */
        public int getWidth() {
            return width;
        }

        /** Gets the height of the thumbnail. */
        public int getHeight() {
            return height;
        }
    }

    /** The type of media that this OEmbed object represents */
    public static enum MediaType {
        /** Represents static photos */
        PHOTO,
        /** Represents playable videos */
        VIDEO,
        /**
         * Responses of this type allow a provider to return any generic embed data (such as title and author_name)
         * without providing either the url or html parameters. The consumer may then link to the resource, using the URL
         * specified in the original request.
         */
        LINK,
        /** Used for rich HTML */
        RICH
    }
}
