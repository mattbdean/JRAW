package net.dean.jraw.fluent;

import net.dean.jraw.ApiException;
import net.dean.jraw.models.FlairTemplate;

import java.util.List;

/**
 * A Reference to a flair. Note that currently this only applies to user flair, not submission flair.
 */
public final class FlairReference extends ElevatedAbstractReference {
    private final String subreddit;

    protected FlairReference(ManagerAggregation managers, String subreddit) {
        super(managers);
        if (subreddit == null)
            throw new IllegalArgumentException("subreddit cannot be null");
        this.subreddit = subreddit;
    }

    /** Gets the current flair the user is using. */
    @NetworkingCall
    public FlairTemplate current() throws ApiException {
        return managers.account().getCurrentFlair(subreddit);
    }

    /** Gets a list of flair the user has the option of using. */
    @NetworkingCall
    public List<FlairTemplate> options() throws ApiException {
        return managers.account().getFlairChoices(subreddit);
    }

    /** Enables or disables flair on this subreddit. */
    @NetworkingCall
    public void enable(boolean flag) throws ApiException {
        managers.account().setFlairEnabled(subreddit, flag);
    }
}
