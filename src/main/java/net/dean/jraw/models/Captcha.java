package net.dean.jraw.models;

import net.dean.jraw.JrawUtils;

import java.net.URL;

/**
 * Represents a Captcha.
 */
public class Captcha {
    private final String id;
    private final URL imageUrl;

    /**
     * Instantiates a new Captcha
     *
     * @param id The captcha's ID
     * @param url The URL to the captcha (as in the image)
     */
    public Captcha(String id, String url) {
        this.id = id;
        this.imageUrl = JrawUtils.newUrl(url);
    }

    /**
     * Gets the captcha's ID
     *
     * @return The captcha's ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the URL to this Captcha's image. The format of the URL is {@code http://reddit.com/captcha/{id}.png}
     * @return The URL to this Captcha's image
     */
    public URL getImageUrl() {
        return imageUrl;
    }

    @Override
    public String toString() {
        return "Captcha {" +
                "id='" + id + '\'' +
                ", imageUrl=" + imageUrl +
                '}';
    }
}
