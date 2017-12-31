package net.dean.jraw.gradle

import org.eclipse.jgit.api.Git
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class VersionCommitTask extends DefaultTask {
    private Git git

    VersionCommitTask() {
        this.git = Git.open(project.rootDir)
    }

    @TaskAction
    void createVersionCommit() {
        this.git.add()
            .addFilepattern(".")
            .call()

        this.git.commit()
            .setAuthor("Matthew Dean", "deanmatthew16@gmail.com")
            .setMessage("Create version v${project.version}")
            .setAllowEmpty(false)
            .call()
    }
}
