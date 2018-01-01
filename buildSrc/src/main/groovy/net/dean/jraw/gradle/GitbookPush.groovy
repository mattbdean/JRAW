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
 * 4. If there are any changes, create a commit for it
 * 5. If createVersionTag is true, creates a tag for the project's version
 * 6. If steps 4 or 5 produced anything, push to remote
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

    /** If true, creates a tag signifying the creation of a new JRAW version */
    boolean createVersionTag = false

    private Git git

    @TaskAction
    void pushToRemote() {
        this.git = GitbookHelper.clone(repository, username, password)
        log("Cloned repository $repository to ${git.repository.directory.absolutePath}")

        // git.repository.directory is the ".git" dir, we want the actual source root
        copyBook(git.repository.directory.parentFile)

        def hasNew = hasNewContent()

        if (!hasNew && !createVersionTag) {
            // nothing to do, skip immediately to cleanup
            log("No new content, skipping")
            throw new StopActionException()
        }

        if (createVersionTag)
            GitbookHelper.createTag(git, "v${project.version}")

        if (hasNew)
            commit(commitMessage(this.createVersionTag))

        push()
        this.git.repository.directory.deleteDir()
    }

    private RevCommit getLatestCommit() {
        return Git.open(new File(project.rootDir, ".git"))
            .log()
            .setMaxCount(1)
            .call()
            .first()
    }

    private String commitMessage(boolean createVersionTag) {
        if (createVersionTag) {
            return "Update documentation for version v${project.version}"
        } else {
            String shortName = getLatestCommit().name().substring(0, 8)
            return "Update documentation from commit ${shortName}\n\n" +
                "See https://github.com/mattbdean/JRAW/commit/${shortName}"
        }
    }

    private void copyBook(File dest) {
        // Recursively copy all files from the compiled book directory to the given destination
        Files.walkFileTree(compiledBookDir.toPath(), new CopyDirVisitor(compiledBookDir.toPath(), dest.toPath()))
        log("Copied all files from ${compiledBookDir.absolutePath} to ${dest.absolutePath}")
    }

    private boolean hasNewContent() {
        return !this.git.status().call().clean
    }

    private void commit(String commitMessage) {
        this.git.add()
            .addFilepattern(".")
            .call()

        this.git.commit()
            .setMessage(commitMessage)
            .call()
        log("Created commit with message '$commitMessage'")
    }

    private void push() {
        this.git.push()
            .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
            .setPushTags()
            .call()
        log("Pushed to remote")
    }

    private void log(String msg) {
        logger.info(msg)
    }
}
