package com.darkguardsman.json.remap.core;

import com.darkguardsman.json.remap.core.errors.MapperException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private static final String FIELD_FIELDS = "fields";
    private static final String FIELD_FIELD = "field";
    private static final String FIELD_ACCESSOR = "accessor";

    private static final String TYPE_STRING = "string";
    private static final String TYPE_INT = "int";
    private static final String TYPE_INTEGER = "integer";
    private static final String TYPE_DOUBLE = "double";
    private static final String TYPE_OBJECT = "object";
    private static final String TYPE_ARRAY = "array";

    private static final String TYPE_AS_IS = "current";
    private static final String TYPE_ARRAY_JOINED = "array.joined";

    private static final String ACTION_FLAT_MAP = "map.flat";
    private static final String ACTION_MAP = "map";

    private static final String ACTION_GET = "get";

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
    public MapperData<T, O, A> loadMapperData(T node) {
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
        final MapperNode<T, O, A> mapperNode = new MapperNode();

        // Load accessor if provided
        final T accessor = handler.getField(mappingObject, FIELD_ACCESSOR);
        mapperNode.setAccessor(loadAccessor(accessor));

        // Load mapper if provided
        mapperNode.setMapper(loadMapper(mappingObject));

        return mapperNode;
    }

    private BiFunction<NodeHandler<T, O, A>, T, T> loadMapper(O mappingObject) {
        // Load mapper based on type
        final String type = handler.asText(handler.getField(mappingObject, FIELD_TYPE));

        // Maps exact value found, useful if we just want to change nesting level or are mapping for an array
        if(type.equalsIgnoreCase(TYPE_AS_IS)) {
            return (factory, input) -> input;
        }
        // Text based
        else if (type.equalsIgnoreCase(TYPE_STRING)) {
            return (factory, input) -> factory.newText(factory.asText(input));
        }
        // Whole number
        else if (type.equalsIgnoreCase(TYPE_INTEGER) || type.equalsIgnoreCase(TYPE_INT)) {
            return (factory, input) -> factory.newInteger(factory.asInt(input));
        }
        // Floating number
        else if (type.equalsIgnoreCase(TYPE_DOUBLE)) {
            return (factory, input) -> factory.newFloating(factory.asDouble(input));
        }
        // Array mapper
        else if (type.equalsIgnoreCase(TYPE_ARRAY)) {
            // TODO create mapper
            // TODO get pre-processor (aka split string into items)
            // TODO use 'item' field to create object node for each sub-object
        }
        // Object mapper
        else if(type.equalsIgnoreCase(TYPE_OBJECT)) {
            final A fields = handler.asArray(handler.getField(mappingObject, FIELD_FIELDS));
            final List<MapperNode<T, O, A>> nodes = handler.asStream(fields, false).map(handler::asObject).map(this::loadNode).toList();
            return (factory, input) -> {
                final O objectNode = factory.newObject();
                nodes.forEach(node -> {
                    factory.setField(objectNode, node.getFieldName(), node.apply(factory, input));
                });
                return objectNode;
            };
        }
        // Converts array into joined string, useful for legacy systems that do string concat elements
        // Often combined with other mappers to get the text value first
        else if(type.equalsIgnoreCase(TYPE_ARRAY_JOINED)) {
            final String joinChar = Optional.ofNullable(handler.getField(mappingObject, "joiner")).map(handler::asText).orElse(",");
            return (factory, input) -> {
                A arrayNode = factory.asArray(input);
                // TODO add user toggle to turn on parallel if item list is large and each item takes a while to parse
                return factory.newText(factory.asStream(arrayNode, false).map(factory::asText).collect(Collectors.joining(joinChar)));
            };
        }
        //TODO add a string template that converts fields into a string EX: "${id}-${nameShort}-${nameFull}"
        //TODO add way to convert string into array using split
        //TODO add a way to create an array from objects that are not the same type
        //TODO add registry to add types dynamically so we drop the long if-else for key:handler
        //TODO add date converter, use built in date-time parser to take an input and output format as string
        //TODO add a toString with formatter for cases like "100.3567" -> "$100.36"

        throw new IllegalArgumentException("mapper configuration failed to load unknown mapper type; node=" + mappingObject);
    }

    private Function<T, T> loadAccessor(T node) {

        // Series of step to run, taking input from the previous step
        if(handler.isArray(node)) {

        }
        // More complex accessors, usually meaning we are pulling data for arrays or flatMapping
        else if(handler.isObject(node)) {
            final O accessorObject = handler.asObject(node);
            final String action = handler.asText(handler.getField(accessorObject, FIELD_TYPE));

            // Converts an array of node into different nodes EX: [{id: 1}, {id: 2}] -> [1, 2]
            if(action.equalsIgnoreCase(ACTION_MAP)) {
                //TODO convert node into other node
            }
            // Converts nested arrays into a more flat array
            else if(action.equalsIgnoreCase(ACTION_FLAT_MAP)) {
                //TODO map then join first layer of arrays
            }
            // Same as string accessor, mostly exists for those that don't want to place a random string in an array of objects
            else if(action.equalsIgnoreCase(ACTION_GET)) {
                return loadAccessorString(handler.asText(handler.getField(accessorObject, FIELD_FIELD)));
            }
            else {
                throw new IllegalArgumentException("mapper configuration failed to load unknown action type; node=" + node);
            }
        }
        return loadAccessorString(handler.asText(node));
    }

    private Function<T, T> loadAccessorString(String accessor) {
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
