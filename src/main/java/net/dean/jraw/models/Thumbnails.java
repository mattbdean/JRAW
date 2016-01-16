package net.dean.jraw.models;

import com.fasterxml.jackson.databind.JsonNode;
import net.dean.jraw.models.meta.JsonProperty;

/**
 * Variations of thumbnails for an image.
 */
public class Thumbnails extends JsonModel {
    public Thumbnails(JsonNode dataNode) {
        super(dataNode);
    }

    @JsonProperty
    public String getId() {
        return data("id");
    }

    /** Gets the source image in full resolution */
    @JsonProperty
    public Image getSource() {
        return getImage(data.get("source"));
    }

    /** Gets an array of all other variations. These Images will be of lower resolution. */
    @JsonProperty
    public Image[] getVariations() {
        JsonNode node = data.get("resolutions");
        Image[] arr = new Image[node.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = getImage(node.get(i));
        }

        return arr;
    }

    private Image getImage(JsonNode node) {
        return new Image(node.get("url").asText(), node.get("width").asInt(), node.get("height").asInt());
    }

    /** An immutable class that represents a thumbnail image. */
    public static final class Image {
        private final String url;
        private final int width;
        private final int height;

        public Image(String url, int width, int height) {
            this.url = url;
            this.width = width;
            this.height = height;
        }

        public String getUrl() {
            return url;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Image image = (Image) o;

            if (width != image.width) return false;
            if (height != image.height) return false;
            return url != null ? url.equals(image.url) : image.url == null;

        }

        @Override
        public int hashCode() {
            int result = url != null ? url.hashCode() : 0;
            result = 31 * result + width;
            result = 31 * result + height;
            return result;
        }

        @Override
        public String toString() {
            return "Image {" +
                    "url='" + url + '\'' +
                    ", width=" + width +
                    ", height=" + height +
                    '}';
        }
    }
}
