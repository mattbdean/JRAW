package net.dean.jraw.gradle

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider

class GitbookHelper {
    /** Clones the repo as the given user into a temporary directory and returns the created Git instance. */
    static Git clone(String repoUrl, String username, String password) {
        File tempDir = File.createTempDir("jraw_tmp_", "_docs")
        return Git.cloneRepository()
            .setURI(repoUrl)
            .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
            .setDirectory(tempDir)
            .call()
    }

    /** Creates a tag with the given name */
    static void createTag(Git git, String name) {
        git.tag()
            .setName(name)
            .setTagger(new PersonIdent("Matthew Dean", "deanmatthew16@gmail.com"))
            .call()
    }
}
