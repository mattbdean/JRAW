package net.dean.jraw.models;

import net.dean.jraw.models.attr.Created;
import net.dean.jraw.models.attr.Distinguishable;
import net.dean.jraw.models.attr.Votable;
import org.codehaus.jackson.JsonNode;

/**
 * This class provides a way to wrap the {@link Submission} and {@link Comment}
 * classes together. Used mostly in {@link net.dean.jraw.pagination.UserContributionPaginator}.
 */
public abstract class Contribution extends Thing implements Created, Distinguishable, Votable {

    /**
     * Instantiates a new Contribution
     *
     * @param dataNode The node to parse data from
     */
    public Contribution(JsonNode dataNode) {
        super(dataNode);
    }
}
