package net.dean.jraw.models;

import net.dean.jraw.models.attr.Distinguishable;
import net.dean.jraw.models.attr.Gildable;
import net.dean.jraw.models.attr.Votable;
import org.codehaus.jackson.JsonNode;

/**
 * This class is used to separate public contributions (submissions and comments) from private contributions (messages)
 */
public abstract class PublicContribution extends Contribution implements Distinguishable, Gildable, Votable {
    /**
     * Instantiates a new PublicContribution
     *
     * @param dataNode The node to parse data from
     */
    public PublicContribution(JsonNode dataNode) {
        super(dataNode);
    }
}
