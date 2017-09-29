package net.dean.jraw.gradle

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.StopActionException
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files

/**
 * This Gradle task pushes a new version of the compiled book (see docs/README.md) GitBook.
 *
 * Here's a rough outline of the process:
 *
 * 1. Identify the latest JRAW commit
 * 2. Clone the repository to a temporary directory
 * 3. Copy all files from `compiledBookDir` to the cloned repo
 * 4. If there are any changes, commit and push
 */
class GitbookPush extends DefaultTask {
    /** Username to use when cloning the repository. */
    String username

    /** Password to use when cloning the repository. May be a GitBook access token if using a GitBook repository. */
    String password

    /** Absolute URL of the Git repo to clone */
    String repository

    /** The directory where the compiled book is located */
    File compiledBookDir

    private Git git

    @TaskAction
    void push() {
        String shortName = getLatestCommit().name().substring(0, 8)
        String commitMessage = "Update documentation from commit ${shortName}\n\n" +
            "See https://github.com/mattbdean/JRAW/commit/${shortName}"

        this.git = cloneRepo()
        // git.repository.directory is the ".git" dir, we want the actual source root
        copyBook(git.repository.directory.parentFile)

        if (!hasNewContent()) {
            // nothing to do, skip immediately to cleanup
            log("No new content, skipping")
            throw new StopActionException()
        }

        commitAndPush(commitMessage)
    }

    @TaskAction
    void cleanUp() {
        this.git.repository.directory.deleteDir()
    }

    private RevCommit getLatestCommit() {
        return Git.open(new File(project.rootDir, ".git"))
            .log()
            .setMaxCount(1)
            .call()
            .first()
    }

    private Git cloneRepo() {
        File tempDir = File.createTempDir("jraw_tmp_", "_docs")
        def git = Git.cloneRepository()
            .setURI(repository)
            .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
            .setDirectory(tempDir)
            .call()
        log("Cloned repository $repository to ${git.repository.directory.absolutePath}")
        return git
    }

    private void copyBook(File dest) {
        // Recursively copy all files from the compiled book directory to the given destination
        Files.walkFileTree(compiledBookDir.toPath(), new CopyDirVisitor(compiledBookDir.toPath(), dest.toPath()))
        log("Copied all files from ${compiledBookDir.absolutePath} to ${dest.absolutePath}")
    }

    private boolean hasNewContent() {
        return !this.git.status().call().clean
    }

    private void commitAndPush(String commitMessage) {
        this.git.add()
            .addFilepattern(".")
            .call()

        this.git.commit()
            .setMessage(commitMessage)
            .call()
        log("Created commit with message '$commitMessage'")

        this.git.push()
            .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
            .call()
        log("Pushed to remote")
    }

    private void log(String msg) {
        logger.info(msg)
    }
}
