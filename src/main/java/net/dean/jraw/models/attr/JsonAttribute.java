package net.dean.jraw.models.attr;

import org.codehaus.jackson.JsonNode;

interface JsonAttribute {

    /**
     * See {@link net.dean.jraw.models.JsonModel#getDataNode()}
     * @return The JsonNode to use for methods annotated with the {@link net.dean.jraw.models.JsonInteraction} annotation
     */
    public JsonNode getDataNode();
}
