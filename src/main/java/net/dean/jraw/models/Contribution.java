package net.dean.jraw.models;

import net.dean.jraw.models.attr.Created;
import org.codehaus.jackson.JsonNode;

import java.util.Date;

/**
 * This class provides a way to wrap {@link Submission}, {@link Comment}, and {@link Message} together.
 */
public abstract class Contribution extends Thing implements Created {

    /**
     * Instantiates a new Contribution
     *
     * @param dataNode The node to parse data from
     */
    public Contribution(JsonNode dataNode) {
        super(dataNode);
    }

    @Override
    public Date getCreated() {
        return _getCreated();
    }

    @Override
    public Date getCreatedUtc() {
        return _getCreatedUtc();
    }
}
