package net.dean.jraw.meta.test

import net.dean.jraw.meta.VersionCreator
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class VersionCreatorTest : Spek({
    it("should produce compilable code") {
        ensureCompilable { VersionCreator.create(it, "1.2.3") }
    }
})
