package net.dean.jraw;

/**
 * This class provides a way to version the library with a uniform standard
 */
public class Version {
	private static final Version v = new Version(0, 1, 0);

	/**
	 * Returns the current version of the library
	 * @return The current version of the library
	 */
	public static Version get() {
		return v;
	}

	private final int major;
	private final int minor;
	private final int micro;
	private final String extra;

	private Version(int major, int minor, int micro) {
		this.major = major;
		this.minor = minor;
		this.micro = micro;
		this.extra = null;
	}

	private Version(int major, int minor, int micro, String extra) {
		this.major = major;
		this.minor = minor;
		this.micro = micro;
		this.extra = extra;
	}

	@Override
	public String toString() {
		return "Version {" +
				"major=" + major +
				", minor=" + minor +
				", micro=" + micro +
				", extra='" + extra + '\'' +
				'}';
	}

	/**
	 * Generates a formatted string representing this Version. The string will be formatted in either of two ways. If {@link #extra}
	 * is not null, then {@code <major>.<minor>.<micro>-<extra>}. If {@link #extra} IS null, then {@literal <major>.<minor>.<micro>}.
	 * @return A formatted string representing this Version
	 */
	public String formatted() {
		// <major>.<minor>.<micro>
		// <major>.<minor>.<micro>-<extra>
		return String.format("%s.%s.%s%s", major, minor, micro, extra == null ? "" : "-" + extra);
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
	 * Gets the micro version (third number)
	 * @return The micro version
	 */
	public int getMicro() {
		return micro;
	}

	/**
	 * Gets the extra detail (ex: "RC-1", "beta")
	 * @return The extra detail
	 */
	public String getExtra() {
		return extra;
	}
}
