package net.dean.jraw.models.meta;

import net.dean.jraw.models.JsonModel;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class provides a standard interface for turning a Jackson JsonNode into a JRAW JsonModel
 * @param <B> The upper bound of the JsonModel to create
 */
public interface JsonSerializer<B extends JsonModel> {
    /**
     * Turns a JsonNode into a JsonModel
     *
     * @param node The node to parse data from. This must be the root node of the object structure. In other words, it
     *             needs to have two children nodes: "kind" and "data".
     * @param clazz The class to make an instance of
     * @param kind The value of the class's Model.kind()
     * @param <T> The type of object to return
     * @return A new JsonModel
     */
    public <T extends B> T parse(JsonNode node, Class<T> clazz, Model.Kind kind);
}
