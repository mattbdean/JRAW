package net.dean.jraw.models;

import net.dean.jraw.models.attr.Created;
import net.dean.jraw.models.meta.ContributionSerializer;
import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Date;

/**
 * This class represents a contribution to the Reddit community (submissions, comments, messages, etc.).
 */
@Model(kind = Model.Kind.ABSTRACT, serializer = ContributionSerializer.class)
public abstract class Contribution extends Thing implements Created {

    /** Instantiates a new Contribution */
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
