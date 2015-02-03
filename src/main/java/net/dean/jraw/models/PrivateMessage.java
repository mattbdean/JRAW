package net.dean.jraw.models;

import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class represents a private message sent from one user to another
 */
@Model(kind = Model.Kind.MESSAGE)
public class PrivateMessage extends Message {
    /**
     * Instantiates a new Thing
     *
     * @param dataNode The node to parse data from
     */
    public PrivateMessage(JsonNode dataNode) {
        super(dataNode);
    }
}
