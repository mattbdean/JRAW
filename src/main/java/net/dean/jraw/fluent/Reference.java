package net.dean.jraw.fluent;

import net.dean.jraw.RedditClient;

/**
 * <p>Provides the basis of the fluent API. Every Reference has four things in common:
 *
 * <ol>
 *     <li>A Reference's constructor always has default (package-protected) visibility
 *     <li>A concrete Reference is always immutable
 *     <li>A Reference is never meant to be constructed by users of the library; they will be instantiated by the
 *         library only inside of the {@code net.dean.jraw.fluent} package.
 *     <li>A Reference does the bare minimum amount of work to complete its task
 * </ol>
 *
 * <p>Each Reference is responsible for doing a minuscule amount of work in the grand scheme of the API.
 */
public interface Reference {
    /** Gets the RedditClient that this Reference uses to send requests */
    RedditClient getRedditClient();
}
