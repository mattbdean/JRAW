package net.dean.jraw;

/**
 * A class to keep track of the current version of JRAW being used
 *
 * For JRAW developers: this class should not be edited by hand. This class can be regenerated through the {@code :meta:update} Gradle task. */
public final class Version {
    private static final String version = "1.0.0";

    /**
     * A semver string like "1.2.3" */
    public static String get() {
        return version;
    }
}
