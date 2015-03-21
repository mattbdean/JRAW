package net.dean.jraw;

/**
 * This class provides a standard way to version the library
 */
public class Version {
    /** If the 'build' field is equal to this constant, then it will be excluded in {@link #formatted()}. */
    public static final int EXCLUDE = -1;
    private static final Version INSTANCE = new Version(0, 7, 0, 3);

    /** Gets the current version of the library */
    public static Version get() {
        return INSTANCE;
    }

    private final int major;
    private final int minor;
    private final int patch;
    private final int build;
    private final String formatted;

    protected Version(int major, int minor, int patch) {
        this(major, minor, patch, EXCLUDE);
    }

    protected Version(int major, int minor, int patch, int build) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.build = build;
        this.formatted = String.format("%s.%s.%s%s", major, minor, patch, build != EXCLUDE ? "." + build : "");
    }

    /**
     * Gets a string representing this Version. If this is a stable release, then the return value of this method will
     * be equal to {@code <major>.<minor>.<patch>}, otherwise it will be equal to
     * {@code <major>.<minor>.<patch>.<build>}.
     *
     * @return A formatted string representing this Version
     */
    public String formatted() {
        return formatted;
    }

    /** Gets the major version (first number) */
    public int getMajor() {
        return major;
    }

    /** Gets the minor version (second number) */
    public int getMinor() {
        return minor;
    }

    /** Gets the patch version (third number) */
    public int getPatch() {
        return patch;
    }

    /** Gets the build number (fourth number). Will be equal to {@link #EXCLUDE} if this is a stable release. */
    public int getBuild() {
        return build;
    }

    @Override
    public String toString() {
        return formatted();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Version version = (Version) other;

        return major == version.major &&
                minor == version.minor &&
                patch == version.patch &&
                build == version.build;

    }

    @Override
    public int hashCode() {
        int result = major;
        result = 31 * result + minor;
        result = 31 * result + patch;
        result = 31 * result + build;
        return result;
    }
}
