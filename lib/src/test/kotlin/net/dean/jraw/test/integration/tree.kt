package net.dean.jraw.test.integration

import net.dean.jraw.test.TestConfig.reddit
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.util.concurrent.TimeUnit

class Tree : Spek({
    it("f") {
        val tree = reddit.submission("7jtexm").comments()
        var res: Any
        for (i in 0..1000)
            tree.walkTree()

        val total = 1_000_000
        val start = System.nanoTime()
        for (i in 0 until total)
            tree.walkTree()

        val elapsed = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS)
        println("${total.toDouble() / elapsed.toDouble()} ops/ms")
    }
})
