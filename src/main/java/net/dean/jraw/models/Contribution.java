package net.dean.jraw.models;

import net.dean.jraw.models.core.Thing;
import org.codehaus.jackson.JsonNode;

/**
 * This class provides a way to wrap the {@link net.dean.jraw.models.core.Submission} and {@link net.dean.jraw.models.core.Comment}
 * classes together. Used mostly
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
