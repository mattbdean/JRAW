package net.dean.jraw;

/**
 * This class provides a standard way to version the library
 */
public class Version {
    private static final Version v = new Version(0, 5, 0);

    /**
     * Returns the current version of the library
     * @return The current version of the library
     */
    public static Version get() {
        return v;
    }

    private final int major;
    private final int minor;
    private final int patch;
    private final boolean snapshot;

    protected Version(int major, int minor, int patch) {
        this(major, minor, patch, false);
    }

    protected Version(int major, int minor, int patch, boolean snapshot) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.snapshot = snapshot;
    }

    @Override
    public String toString() {
        return "Version {" +
                "major=" + major +
                ", minor=" + minor +
                ", patch=" + patch +
                ", snapshot=" + snapshot +
                '}';
    }

    /**
     * Generates a formatted string representing this Version in the format of {@code <major>.<minor>.<patch>}
     * @return A formatted string representing this Version
     */
    public String formatted() {
        return String.format("%s.%s.%s%s", major, minor, patch, snapshot ? "-SNAPSHOT" : "");
    }

    /**
     * Gets the major version (first number)
     * @return The major version
     */
    public int getMajor() {
        return major;
    }

    /**
     * Gets the minor version (second number)
     * @return The minor version
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Gets the patch version (third number)
     * @return The patch version
     */
    public int getPatch() {
        return patch;
    }

    /**
     * Checks if this build is a snapshot build
     * @return If this version is a snapshot
     */
    public boolean isSnapshot() {
        return snapshot;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Version version = (Version) other;

        return major == version.major &&
                minor == version.minor &&
                patch == version.patch &&
                snapshot == version.snapshot;

    }

    @Override
    public int hashCode() {
        int result = major;
        result = 31 * result + minor;
        result = 31 * result + patch;
        result = 31 * result + (snapshot ? 1 : 0);
        return result;
    }
}
