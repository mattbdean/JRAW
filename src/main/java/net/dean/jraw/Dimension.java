package net.dean.jraw;

/**
 * Dimension.
 *
 * @author Moshe on 2014/12/30.
 */
public class Dimension {

    public int width;
    public int height;

    public Dimension() {
    }

    public Dimension(int w, int h) {
        width = w;
        height = h;
    }

    public Dimension(Dimension p) {
        this.width = p.width;
        this.height = p.height;
    }

    public final void set(int w, int h) {
        width = w;
        height = h;
    }

    public final void set(Dimension d) {
        this.width = d.width;
        this.height = d.height;
    }

    public final boolean equals(int w, int h) {
        return this.width == w && this.height == h;
    }

    public final boolean equals(Object o) {
        return o instanceof Dimension && (o == this || equals(((Dimension) o).width, ((Dimension) o).height));
    }
}
