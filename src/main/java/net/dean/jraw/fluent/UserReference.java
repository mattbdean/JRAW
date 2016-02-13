package net.dean.jraw.fluent;

import net.dean.jraw.models.Trophy;
import net.dean.jraw.paginators.UserContributionPaginator;

import java.util.List;

/**
 * Reference to a user
 */
public class UserReference extends ElevatedAbstractReference {
    protected final String user;

    UserReference(ManagerAggregation managers, String user) {
        super(managers);
        this.user = user;
    }

    /** Gets the user's trophy case */
    @NetworkingCall
    public List<Trophy> trophyCase() {
        return managers.reddit().getTrophies(user);
    }

    public UserContributionPaginator overview() {
        return new UserContributionPaginator(managers.reddit(), "overview", user);
    }

    public UserContributionPaginator comments() {
        return new UserContributionPaginator(managers.reddit(), "comments", user);
    }

    public UserContributionPaginator submitted() {
        return new UserContributionPaginator(managers.reddit(), "submitted", user);
    }

    public UserContributionPaginator gilded() {
        return new UserContributionPaginator(managers.reddit(), "gilded", user);
    }

    /** Gets the user's name */
    public String getUser() {
        return user;
    }
}
