package net.dean.jraw.models;

import net.dean.jraw.JrawUtils;
import org.codehaus.jackson.JsonNode;

import java.net.URL;

/**
 * Represents media provided by an oEmbed-style JSON object. Documentation in this class is borrowed heavily from the
 * <a href="http://oembed.com/#section2.3">oEmbed website</a>.
 */
public class OEmbed extends JsonModel {

    /**
     * Instantiates a new OEmbed object
     *
     * @param oembedNode The node to parse data from. The key is usually "oembed".
     */
    public OEmbed(JsonNode oembedNode) {
        super(oembedNode);
    }

    /**
     * The resource type
     * @return The resource type
     */
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

    /**
     * A text title, describing the resource
     * @return The title of the content
     */
    @JsonProperty(nullable = true)
    public String getTitle() {
        return data("title");
    }

    /**
     * The oEmbed version number. This must be 1.0.
     * @return The oEmbed version number
     */
    @JsonProperty(nullable = true)
    public String getVersion() {
        return data("version");
    }

    /**
     * The name of the author/owner of the resource.
     * @return The author of the resource
     */
    @JsonProperty(nullable = true)
    public String getAuthorName() {
        return data("author_name");
    }

    /**
     * A URL for the author/owner of the resource.
     * @return The author's URL
     */
    @JsonProperty(nullable = true)
    public URL getAuthorUrl() {
        return data("author_url", URL.class);
    }

    /**
     * The name of the resource provider.
     * @return The name of the resource provider.
     */
    @JsonProperty(nullable = true)
    public String getProviderName() {
        return data("provider_name");
    }

    /**
     * The url of the resource provider.
     * @return The url of the resource provider.
     */
    @JsonProperty(nullable = true)
    public URL getProviderUrl() {
        return data("provider_url", URL.class);
    }

    /**
     * The <i>suggested</i> cache lifetime for this resource, in seconds. Consumers may choose to use this value or not.
     * @return The suggest cache lifetime in seconds
     */
    @JsonProperty(nullable = true)
    public Integer getCacheAge() {
        return data("cache_age", Integer.class);
    }

    /**
     * Gets the thumbnail associated with this resource
     * @return The thumbnail associated with this resource
     */
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
     * Required when the media type is not {@link net.dean.jraw.models.OEmbed.MediaType#LINK}. Width in pixels required
     * to display the video, image, or HTML.
     *
     * @return The width in pixels
     */
    @JsonProperty(nullable = true)
    public Integer getWidth() {
        return data("width", Integer.class);
    }

    /**
     * Required when the media type is not {@link net.dean.jraw.models.OEmbed.MediaType#LINK}. Height in pixels required
     * to display the video, image, or HTML.
     *
     * @return The height in pixels
     */
    @JsonProperty(nullable = true)
    public Integer getHeight() {
        return data("height", Integer.class);
    }

    /**
     * Required when the media type is {@link net.dean.jraw.models.OEmbed.MediaType#PHOTO}. The source URL of the image
     *
     * @return The image's URL
     */
    @JsonProperty(nullable = true)
    public URL getUrl() {
        if (data.has("url"))
            return JrawUtils.newUrl(data("url"));
        return null;
    }

    /**
     * Required when the media type is {@link net.dean.jraw.models.OEmbed.MediaType#VIDEO} or
     * {@link net.dean.jraw.models.OEmbed.MediaType#RICH}
     *
     * @return The HTML required to embed a video player (if video) or display the resource (if rich)
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

        /**
         * A URL to a thumbnail image representing the resource
         * @return The thumbnail URL
         */
        public URL getUrl() {
            return url;
        }

        /**
         * The width of the optional thumbnail.
         * @return The width of the optional thumbnail.
         */
        public int getWidth() {
            return width;
        }

        /**
         * The height of the optional thumbnail.
         * @return The height of the optional thumbnail.
         */
        public int getHeight() {
            return height;
        }
    }

    /**
     * The type of media that this OEmbed object represents
     */
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
