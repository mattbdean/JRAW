package net.dean.jraw.meta

import com.grosner.kpoet.*
import com.squareup.javapoet.ClassName
import java.io.File

class EnumCreator(val endpoints: List<ParsedEndpoint>, val indent: Int = 4) {
    fun writeTo(out: File) = createJavaFile().writeTo(out)
    fun writeTo(out: Appendable) = createJavaFile().writeTo(out)

    private fun createJavaFile() =
        // Create the file definition belonging to package net.dean.jraw and configuring the indent using spaces
        javaFile(PACKAGE, {
            indent(" ".repeat(indent))
            skipJavaLangImports(true)
            `import static`(ClassName.get(PACKAGE, ENUM_NAME, INNER_CLASS_NAME), SUBREDDIT_PREFIX_CONSTANT_NAME)
        }) {
            // public enum Endpoint
            enum(ENUM_NAME) { modifiers(public)
                // Add Javadoc to the enum
                javadoc("This is a dynamically generated enumeration of all reddit API endpoints.\n\n")
                javadoc(
                    "For JRAW developers: this class should not be edited by hand. This class can be regenerated " +
                        "through the ${code(":meta:update")} Gradle task.")

                // Dynamically add a enum value for each endpoint
                for (e in endpoints) {
                    case(enumName(e), "\$S, \$L, \$S", e.method, enumPath(e), e.oauthScope) {
                        javadoc(javadocFor(e))
                    }
                }

                // Declare the inner static class with the 'optional subreddit' constant
                addType(`public class`(INNER_CLASS_NAME) { modifiers(static)
                    `public static final field`(String::class, SUBREDDIT_PREFIX_CONSTANT_NAME, { this.`=`(SUBREDDIT_PREFIX_CONSTANT_VALUE.S) })
                })

                // Declare three private final fields "method", "path", and "scope"
                `private final field`(String::class, "method")
                `private final field`(String::class, "path")
                `private final field`(String::class, "scope")

                // Create constructor which takes those three fields as parameters
                `constructor`(`final param`(String::class, "method"),
                    `final param`(String::class, "path"),
                    `final param`(String::class, "scope")) {
                    statement("this.method = method")
                    statement("this.path = path")
                    statement("this.scope = scope")
                }

                // Create getters for the "path" and "method" fields
                `public`(String::class, "getPath") {
                    javadoc("Gets this Endpoint's path, e.g. ${code("/api/comment")}")
                    `return`("this.path")
                }

                `public`(String::class, "getMethod") {
                    javadoc("Gets this Endpoint's HTTP method (\"GET\", \"POST\", etc.)")
                    `return`("this.method")
                }

                `public`(String::class, "getScope") {
                    javadoc("Gets the OAuth2 scope required to use this endpoint")
                    `return`("this.scope")
                }
            }
        }

    companion object {
        const val ENUM_NAME = "Endpoint"
        const val PACKAGE = "net.dean.jraw"
        const val INNER_CLASS_NAME = "Constant"
        const val SUBREDDIT_PREFIX_CONSTANT_NAME = "OPTIONAL_SUBREDDIT"
        const val SUBREDDIT_PREFIX_CONSTANT_VALUE = "[/r/{subreddit}]"
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

        private fun enumPath(e: ParsedEndpoint) =
            if (e.subredditPrefix)
                "$SUBREDDIT_PREFIX_CONSTANT_NAME + ${e.path.S}"
            else
                e.path.S

        private fun javadocFor(e: ParsedEndpoint): String =
            "Represents the endpoint ${code(e.method + " " + e.path)}. Requires OAuth scope '${e.oauthScope}'. See " +
                link("here", e.redditDocLink) + " for more information"

        private fun link(displayText: String, url: String) =
            "<a href=\"$url\">$displayText</a>"

        private fun code(text: String) =
            "{@code $text}"
    }
}
