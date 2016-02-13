package net.dean.jraw.fluent;

import net.dean.jraw.models.AccountPreferences;
import net.dean.jraw.models.KarmaBreakdown;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.paginators.UserContributionPaginator;
import net.dean.jraw.paginators.UserSubredditsPaginator;

import java.util.List;

/**
 * A special subclass of UserReference of the currently-authenticated user. Uses the {@link ManagerAggregation}'s
 * {@link net.dean.jraw.managers.AccountManager} to provide extra functionality available only to that user.
 */
public final class AuthenticatedUserReference extends UserReference {
    AuthenticatedUserReference(ManagerAggregation managers) {
        super(managers, managers.reddit().getAuthenticatedUser());
    }

    @NetworkingCall
    public KarmaBreakdown karmaBreakdown() {
        return managers.account().getKarmaBreakdown();
    }

    @NetworkingCall
    public AccountPreferences accountPreferences() {
        return managers.account().getPreferences();
    }

    @NetworkingCall
    public List<Subreddit> subscribedSubreddits() {
        return new UserSubredditsPaginator(managers.reddit(), "subscriber").accumulateMergedAll();
    }

    /** Gets a reference to the user's inbox */
    public InboxReference inbox() {
        return new InboxReference(managers);
    }

    public UserContributionPaginator upvoted() {
        return new UserContributionPaginator(managers.reddit(), "upvoted", user);
    }

    public UserContributionPaginator downvoted() {
        return new UserContributionPaginator(managers.reddit(), "downvoted", user);
    }

    public UserContributionPaginator hidden() {
        return new UserContributionPaginator(managers.reddit(), "hidden", user);
    }

    public UserContributionPaginator saved() {
        return new UserContributionPaginator(managers.reddit(), "saved", user);
    }
}
