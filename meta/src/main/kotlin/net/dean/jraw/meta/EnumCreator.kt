package net.dean.jraw.meta

import com.grosner.kpoet.*
import java.io.File

class EnumCreator(val endpoints: List<ParsedEndpoint>, val indent: Int = 4) {
    fun writeTo(out: File) = createJavaFile().writeTo(out)
    fun writeTo(out: Appendable) = createJavaFile().writeTo(out)

    private fun createJavaFile() =
        // Create the file definition belonging to package net.dean.jraw and configuring the indent using spaces
        javaFile(PACKAGE, { indent(" ".repeat(indent)); skipJavaLangImports(true) }) {
            // public enum Endpoint
            enum(ENUM_NAME) { modifiers(public)
                // Add Javadoc to the enum
                javadoc("This is a dynamically generated enumeration of all reddit API endpoints.\n\n")
                javadoc(
                    "For JRAW developers: this class should not be edited by hand. This class can be regenerated " +
                        "through the `:meta:update` Gradle task.")

                // Dynamically add a enum value for each endpoint
                for (e in endpoints) {
                    // Not sure how to specify two parameters, this'll have to do for now
                    case(enumName(e), "${e.method} ${e.path}".S) {
                        javadoc(javadocFor(e))
                    }
                }

                // Declare two private final fields "method" and "path"
                `private final field`(String::class, "method")
                `private final field`(String::class, "path")

                // Create constructor which takes those two fields as parameters
                `constructor`(`final param`(String::class, "identifier")) {
                    statement("String[] parts = identifier.split(\" \")")
                    statement("this.method = parts[0]")
                    statement("this.path = parts[1]")
                }


                // Create getters for the "path" and "method" fields
                `public`(String::class, "getPath") {
                    javadoc("Gets this Endpoint's path, e.g. `/api/comment`")
                    `return`("this.path")
                }

                `public`(String::class, "getMethod") {
                    javadoc("Gets this Endpoint's HTTP method (\"GET\", \"POST\", etc.)")
                    `return`("this.method")
                }
            }
        }


    companion object {
        const val ENUM_NAME = "Endpoint"
        const val PACKAGE = "net.dean.jraw"
        @JvmField val RELATIVE_OUTPUT_FILE = PACKAGE.replace('.', File.separatorChar) + "/$ENUM_NAME.java"

        private val stripPrefixes = listOf("/api/v1/", "/api/", "/")

        private fun enumName(e: ParsedEndpoint): String {
            var name = e.path

            stripPrefixes
                .asSequence()
                .filter { name.startsWith(it) }
                .forEach { name = name.substring(it.length) }

            if (name.startsWith("r/{subreddit}"))
                name = "subreddit" + name.substring("r/{subreddit}".length)

            return (e.method + '_' + name.replace('/', '_'))
                .toUpperCase()
                .replace("{", "")
                .replace("}", "")
        }

        private fun javadocFor(e: ParsedEndpoint): String =
            "Represents the endpoint `${e.method} ${e.path}`. Requires OAuth scope '${e.oauthScope}'. See " +
                "[here](${e.redditDocLink}) for more information"
    }
}
