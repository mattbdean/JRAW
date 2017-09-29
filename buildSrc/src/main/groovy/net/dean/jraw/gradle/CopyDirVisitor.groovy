package net.dean.jraw.gradle

import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.BasicFileAttributes

/**
 * Recursively copies every file from one directory to another. Totally didn't steal this off of StackOverflow...
 */
class CopyDirVisitor extends SimpleFileVisitor<Path> {
    private final Path fromPath
    private final Path toPath

    CopyDirVisitor(Path fromPath, Path toPath) {
        this.fromPath = fromPath
        this.toPath = toPath
    }

    @Override
    FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Path targetPath = toPath.resolve(fromPath.relativize(dir))
        if (!Files.exists(targetPath))
            Files.createDirectory(targetPath)

        return FileVisitResult.CONTINUE
    }

    @Override
    FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.copy(file, toPath.resolve(fromPath.relativize(file)), StandardCopyOption.REPLACE_EXISTING)
        return FileVisitResult.CONTINUE
    }
}
