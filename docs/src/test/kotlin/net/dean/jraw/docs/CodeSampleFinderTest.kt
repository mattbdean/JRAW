
import com.winterbe.expekt.should
import net.dean.jraw.docs.CodeSampleFinder
import net.dean.jraw.docs.CodeSampleRef
import org.intellij.lang.annotations.Language
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class CodeSampleFinderTest : Spek({
    @Language("java")
    val testClass = """
public class TestClass {
    @CodeSample
    private static void first() {
        // do a thing
        String foo = 4;
        int bar = foo * 2;
    }

    @CodeSample
    private static void second() {
        // In the real docs we would instantiate this properly
        RedditClient reddit = null;

        // Get info about logged-in user
        reddit.me().about();
    }
}
"""

    describe("find") {
        it("should recognized both @CodeSample and @CodeSampleGroup annotations") {
            CodeSampleFinder.find(testClass).should.equal(listOf(
                CodeSampleRef("TestClass.first", listOf(
                    "// do a thing",
                    "String foo = 4;",
                    "int bar = foo * 2;")
                ),
                CodeSampleRef("TestClass.second", listOf(
                    "// In the real docs we would instantiate this properly",
                    "RedditClient reddit = null;",
                    "// Get info about logged-in user",
                    "reddit.me().about();")
                )
            ))
        }
    }
})
