package net.dean.jraw.models.meta;

import net.dean.jraw.JrawUtils;
import net.dean.jraw.models.JsonModel;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for managing the creation of JsonModels. It keeps a cache of common {@link JsonSerializer}s
 * and {@link Model} classes. This class is a singleton.
 */
public class ModelManager {
    public static final Class<? extends JsonSerializer> DEFAULT_SERIALIZER = DefaultJsonSerializer.class;
    private static ModelManager instance = new ModelManager();
    public static ModelManager getInstance() { return instance; }

    /**
     * Convenience method equivalent to {@link #getInstance()}.{@link #parse(JsonNode, Class)}
     */
    public static <T extends JsonModel> T create(JsonNode rootNode, Class<T> expectedClass) {
        return getInstance().parse(rootNode, expectedClass);
    }

    private Map<Class<?>, Model> modelCache;
    private Map<Class<? extends JsonSerializer>, JsonSerializer> serializers;

    private ModelManager() {
        this.modelCache = new HashMap<>();
        this.serializers = new HashMap<>();
    }

    /**
     * Asserts that the given class contains a {@link Model} annotation and returns it
     */
    private Model ensureModelClass(Class<?> clazz) {
        // modelCache will only contain classes that have the @Model annotation
        if (!modelCache.containsKey(clazz)) {
            if (!clazz.isAnnotationPresent(Model.class)) {
                throw new IllegalArgumentException(String.format("Class %s is not annotated with %s",
                        clazz.getName(), Model.class.getName()));
            }
        }

        return clazz.getAnnotation(Model.class);
    }

    /**
     * Gets the instance of the JsonSerializer for the given Model class.
     * @param clazz The class that has the Model annotation
     */
    private JsonSerializer getSerializer(Class<? extends JsonModel> clazz) {
        ensureModelClass(clazz);
        Class<? extends JsonSerializer> serClass = clazz.getAnnotation(Model.class).serializer();
        if (serializers.containsKey(serClass)) {
            return serializers.get(serClass);
        }

        JsonSerializer serializer;
        try {
            serializer = serClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(
                    "JsonSerializer " + clazz.getName() + " has no default constructor or is abstract", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        serializers.put(serClass, serializer);
        return serializer;
    }

    /**
     * Gets the Model annotation for the given class
     */
    private Model getModel(Class<?> modelClass) {
        ensureModelClass(modelClass);

        if (modelCache.containsKey(modelClass)) {
            return modelCache.get(modelClass);
        }

        Model model = modelClass.getAnnotation(Model.class);
        modelCache.put(modelClass, model);
        return model;
    }

    /**
     * Validates that the given JsonNode can be applied to the given class. If the value of {@link Model#kind()} does
     * not match the value of the "kind" node, then an IllegalArgumentException is thrown.
     *
     * @param rootNode The root node of the JSON model. Must contain two children: "kind" and "data".
     * @param expectedClass The class to use as validation.
     * @throws IllegalArgumentException If the root node does not have a "kind" node or if the expected value for that
     *                                  node was not found.
     */
    public void validate(JsonNode rootNode, Class<?> expectedClass) {
        Model type = getModel(expectedClass);
        if (!rootNode.has("kind")) {
            throw new IllegalArgumentException("JsonNode does not have a 'kind' child");
        }

        String actual = rootNode.get("kind").asText();
        String expected = type.kind().getValue();
        if (!rootNode.get("kind").asText().equals(type.kind().getValue())) {
            throw new IllegalArgumentException(String.format("Expected kind was not found. Expected '%s', got '%s'",
                    expected, actual));
        }
    }

    /**
     * Parses a JsonNode into a JsonModel. The given class must be annotated with {@link Model}. If the Model's kind()
     * is {@link Model.Kind#ABSTRACT} or {@link Model.Kind#NONE}, then the JsonNode will not be validated via
     * {@link #validate(JsonNode, Class)}.
     *
     * @param rootNode The root JSON node. Must contain two children: "data" and "kind", unless the {@link Model}'s
     *                 {@link Model#kind() kind} is {@link Model.Kind#NONE}
     * @param expectedClass The class to map this data to
     * @param <T> What type of JsonModel to return
     * @return A new JsonModel
     */
    @SuppressWarnings("unchecked")
    public <T extends JsonModel> T parse(JsonNode rootNode, Class<T> expectedClass) {
        Model model = ensureModelClass(expectedClass);
        Model.Kind kind = model.kind();
        // Validate only if requested or the kind is ABSTRACT or NONE, since NONE types do not have a "kind" node and
        // will fail validation
        if (model.validate() && !(kind.equals(Model.Kind.ABSTRACT) || kind.equals(Model.Kind.NONE))) {
            validate(rootNode, expectedClass);
        }

        JsonSerializer serializer = getSerializer(expectedClass);
        if (kind.equals(Model.Kind.ABSTRACT)) {
            Model.Kind effectiveKind = Model.Kind.getByValue(rootNode.get("kind").asText());
            JrawUtils.logger().debug("Mapping abstract @Model to {}", effectiveKind.getDefaultClass().getName());
            // Try to serialize the abstract
            return (T) serializer.parse(rootNode, effectiveKind.getDefaultClass(), effectiveKind);
        }
        return (T) serializer.parse(rootNode, expectedClass, kind);
    }
}
