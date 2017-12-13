package net.dean.jraw.http

import java.io.PrintStream

/**
 * Writes data to a PrintStream
 *
 * @property out The PrintStream to use. Defualts to System.out.
 */
class PrintStreamLogAdapter(private val out: PrintStream = System.out) : LogAdapter {
    override fun writeln(data: String) {
        out.println(data)
    }
}
