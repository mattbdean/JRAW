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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dimension dimension = (Dimension) o;

        if (height != dimension.height) return false;
        if (width != dimension.width) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        return result;
    }

    @Override
    public String toString() {
        return "Dimension {" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
