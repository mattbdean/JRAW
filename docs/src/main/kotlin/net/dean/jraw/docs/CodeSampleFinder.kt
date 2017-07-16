package net.dean.jraw.docs

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import java.io.File

object CodeSampleFinder {
    fun find(javaClassSource: String) = findCodeSamples(JavaParser.parse(javaClassSource))
    fun find(javaFile: File) = findCodeSamples(JavaParser.parse(javaFile))

    private fun findCodeSamples(cu: CompilationUnit): List<CodeSampleRef> {
        val visitor = MethodVisitor()
        visitor.visit(cu, null)

        return visitor.codeSamples
    }

    private class MethodVisitor : VoidVisitorAdapter<Void>() {
        val codeSamples = mutableListOf<CodeSampleRef>()

        override fun visit(n: MethodDeclaration, arg: Void?) {
            // Try to find a @CodeSample annotation present on the method, otherwise continue visiting other methods
            n.annotations.firstOrNull { it.nameAsString == CodeSample::class.java.simpleName } ?:
                return super.visit(n, arg)

            val methodName = n.name.asString()

            val body = n.childNodes
                // Attempt to find a child of this MethodDeclaration that is a BlockStmt
                .firstOrNull { it is BlockStmt }?.childNodes
                // Split each declaration by the newline character, which will be included if there are comments involved
                ?.map { it.toString().split("\n") }
                // Flatten the List<List<String>> into a List<String>
                ?.flatten()
                    // If we somehow couldn't find a body for the method, continue to the next method
                    ?: return super.visit(n, arg)

            val fullName: String

            // Assume that the method declaration has a parent node that is a class declaration
            if (n.parentNode.get() is ClassOrInterfaceDeclaration) {
                val parentClass = n.parentNode.get() as ClassOrInterfaceDeclaration

                // Try to find a CodeSampleGroup annotation on the parent class
                fullName = parentClass.nameAsString + "." + methodName
            } else {
                throw IllegalArgumentException("Expected method to be declared inside of a class")
            }

            // Add our findings to the list
            codeSamples.add(CodeSampleRef(fullName, body))

            return super.visit(n, arg)
        }
    }
}
