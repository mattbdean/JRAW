package net.dean.jraw.models;

import net.dean.jraw.JrawUtils;

import java.net.URL;

/**
 * Represents a Captcha (an acronym for "Completely Automated Public Turing test to tell Computers and Humans Apart").
 * These are required by the Reddit API for some actions that are susceptible to spam, like creating subreddits or
 * accounts.
 */
public class Captcha {
    private final String id;
    private final URL imageUrl;

    /**
     * Instantiates a new Captcha
     *
     * @param id The captcha's ID
     */
    public Captcha(String id) {
        this.id = id;
        this.imageUrl = JrawUtils.newUrl("https://www.reddit.com/captcha/" + JrawUtils.urlEncode(id) + ".png");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Captcha captcha = (Captcha) o;

        return !(id != null ? !id.equals(captcha.id) : captcha.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
