package net.dean.jraw.http

/**
 * Used pretty much exclusively by HttpLogger to log strings.
 */
interface LogAdapter {
    /**
     * Writes data as its own line. For example, when writing to stdout, the given data should be followed by a newline
     * character (\n on *nix)
     */
    fun writeln(data: String)
}
