package com.darkguardsman.json.remap.core;

import com.darkguardsman.json.remap.core.errors.MapperException;
import lombok.AllArgsConstructor;

import java.util.function.Function;

/**
 * Used to load the mapping data from JSON file
 *
 * @param <T> type of node to use
 */
@AllArgsConstructor
public class MapperLoader<T extends Object, O extends T, A extends T> { //TODO convert to interface so we can have GSON or Jackson direct write to objects for us

    private static final String FIELD_KEY = "key";
    private static final String FIELD_MAPPING = "mapping";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_ACCESSOR = "accessor";

    private static final String TYPE_STRING = "string";
    private static final String TYPE_INT = "int";
    private static final String TYPE_INTEGER = "integer";
    private static final String TYPE_DOUBLE = "double";
    private static final String TYPE_OBJECT = "object";
    private static final String TYPE_ARRAY = "array";

    /**
     * Handler to interact with JSON nodes, should match what is expected for mapping files later in the process
     */
    private final NodeHandler<T, O, A> handler;

    /**
     * Used to load a configuration file used to generate a mapper
     *
     * @param node containing the configuration file
     * @return root mapper node
     */
    public MapperData<T, O, A> loadMapper(T node) {
        if (handler.isObject(node)) {
            final O objectNode = handler.asObject(node);
            if (!handler.hasField(objectNode, FIELD_KEY)) {
                throw new IllegalArgumentException("mapper configuration is missing 'key' field");
            }
            if (!handler.hasField(objectNode, FIELD_MAPPING)) {
                throw new IllegalArgumentException("mapper configuration is missing 'mapping' field");
            }
            final String key = handler.asText(handler.getField(objectNode, FIELD_KEY));
            final T mapping = handler.getField(objectNode, FIELD_MAPPING);
            if (handler.isObject(mapping)) {
                final MapperData<T, O, A> data = new MapperData();
                data.setKey(key);
                data.setRootNode(loadNode(handler.asObject(mapping)));
                return data;
            }
            throw new IllegalArgumentException("mapper configuration needs 'mapping' field to be an object");
        }
        throw new IllegalArgumentException("mapper configuration needs to be a JSON object");
    }

    private MapperNode<T, O, A> loadNode(O mappingObject) {
        final String type = handler.asText(handler.getField(mappingObject, FIELD_TYPE));
        final String accessor = handler.asText(handler.getField(mappingObject, FIELD_ACCESSOR));

        final MapperNode<T, O, A> mapperNode = new MapperNode();
        mapperNode.setAccessor(loadAccessor(accessor));

        if (type.equalsIgnoreCase(TYPE_STRING)) {
            mapperNode.setMapper((factory, input) -> factory.newText(factory.asText(input)));
        } else if (type.equalsIgnoreCase(TYPE_INTEGER) || type.equalsIgnoreCase(TYPE_INT)) {
            mapperNode.setMapper((factory, input) -> factory.newInteger(factory.asInt(input)));
        } else if (type.equalsIgnoreCase(TYPE_DOUBLE)) {
            mapperNode.setMapper((factory, input) -> factory.newFloating(factory.asDouble(input)));
        } else {
            throw new IllegalArgumentException("mapper configuration failed to load unknown mapper type; node=" + mapperNode);
        }

        return mapperNode;
    }

    private Function<T, T> loadAccessor(String accessor) {
        if (accessor != null) {
            String[] split = accessor.split("[.]");
            return (node) -> {
                T value = node;

                // Dive fields in attempt to resolve full path
                for (String field : split) {

                    // Validate we are an object, if not throw an error as it likely means our input is invalid or mapper is outdated
                    if (handler.isObject(value)) {
                        value = handler.getField(handler.asObject(value), field);
                        if (value == null) {
                            return null;
                        }
                    } else {
                        final String formattedMessage = String.format("Failed to access '%s' as field '%s' was not an object, node=%s", accessor, field, value);
                        throw new MapperException(formattedMessage);
                    }
                }
                return value;
            };
        }
        return null;
    }
}
