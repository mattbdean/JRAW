package net.dean.jraw.models.meta;

import net.dean.jraw.models.JsonModel;
import com.fasterxml.jackson.databind.JsonNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * This class attempts to serialize a JsonModel by invoking a constructor that takes a single JsonNode as an argument.
 * Using the JsonSerializer on an abstract type or a class that does not provide an acceptable constructor will cause
 * an exception to be thrown.
 */
public class DefaultJsonSerializer implements JsonSerializer<JsonModel> {

    @Override
    public <T extends JsonModel> T parse(JsonNode node, Class<T> clazz, Model.Kind kind) {
        try {
            // Instantiate a generic Thing
            Constructor<T> constructor = clazz.getConstructor(JsonNode.class);
            return constructor.newInstance(node.get("data"));
        } catch (NoSuchMethodException |
                InstantiationException |
                IllegalAccessException |
                InvocationTargetException e) {
            // Holy exceptions Batman!
            throw new IllegalStateException(String.format("Could not create a new %s", clazz.getName()), e);
        }
    }
}
