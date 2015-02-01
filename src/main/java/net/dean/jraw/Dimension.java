package net.dean.jraw;

/**
 * Represents an immutable dimension, consisting of a width and height
 */
public final class Dimension {
    private final int width;
    private final int height;

    public Dimension(int w, int h) {
        width = w;
        height = h;
    }

    public Dimension(Dimension d) {
        this.width = d.width;
        this.height = d.height;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public boolean equals(int w, int h) {
        return this.width == w && this.height == h;
    }

    public boolean equals(Object o) {
        return o instanceof Dimension && (o == this || equals(((Dimension) o).width, ((Dimension) o).height));
    }
}
