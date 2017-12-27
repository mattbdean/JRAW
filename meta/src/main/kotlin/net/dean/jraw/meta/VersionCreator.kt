package net.dean.jraw.meta

import com.grosner.kpoet.*
import net.dean.jraw.meta.EnumCreator.Companion.PACKAGE
import net.dean.jraw.meta.EnumCreator.Companion.code
import java.io.File
import javax.lang.model.element.Modifier

/**
 * Creates Version.java in lib/src/gen/java
 */
object VersionCreator {
    fun create(srcRoot: File, versionString: String, indent: Int = 4): File {
        javaFile(PACKAGE, { indent(" ".repeat(indent)); skipJavaLangImports(true) }) {
            `public final class`(CLASS_NAME) {
                javadoc("A class to keep track of the current version of JRAW being used\n\n")
                javadoc(
                    "For JRAW developers: this class should not be edited by hand. This class can be regenerated " +
                        "through the ${code(":meta:update")} Gradle task.")

                field(String::class, "version") {
                    addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                    initializer(versionString.S)
                }

                `public static`(String::class, "get") {
                    javadoc("A semver string like \"1.2.3\"")
                    statement("return version")
                }
            }
        }.writeTo(srcRoot)

        return File(srcRoot, PACKAGE.replace('.', File.separatorChar) + "/$CLASS_NAME.java")
    }

    const val CLASS_NAME = "Version"
}
