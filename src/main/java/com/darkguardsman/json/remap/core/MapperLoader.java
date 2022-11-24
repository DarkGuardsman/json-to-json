package com.darkguardsman.json.remap.core;

import com.darkguardsman.json.remap.core.accessors.AccessorGet;
import com.darkguardsman.json.remap.core.errors.MapperException;
import com.darkguardsman.json.remap.core.imp.IAccessorFunc;
import com.darkguardsman.json.remap.core.imp.IMapperFunc;
import com.darkguardsman.json.remap.core.imp.INodeHandler;
import com.darkguardsman.json.remap.core.mappers.*;
import com.darkguardsman.json.remap.core.nodes.MapperNode;
import com.darkguardsman.json.remap.core.nodes.MapperNodeArray;
import com.darkguardsman.json.remap.core.nodes.MapperNodeObject;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Used to load the mapping data from JSON file
 *
 * @param <T> type of node to use
 */
@Data
@AllArgsConstructor
public class MapperLoader<T extends Object, O extends T, A extends T> { //TODO convert to interface so we can have GSON or Jackson direct write to objects for us

    /** Type to simple mappers */
    private final Map<String, IMapperFunc<T, O, A>> mappers = new HashMap(); //TODO see if there is a better map for strings

    /** Type to mapper builders, for more dynamic mappers that require input data or provide settings */
    private final Map<String, BiFunction<MapperLoader<T, O, A>, O, IMapperFunc<T, O, A>>> mapperBuilders = new HashMap();
    private static final String FIELD_KEY = "key";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_MAPPER = "mapper";
    private static final String FIELD_ACCESSOR = "accessor";

    // Array constant
    private static final String TYPE_ARRAY = "array";
    private static final String FIELD_ARRAY_ITEM = "item";

    // Object constant
    private static final String TYPE_OBJECT = "object";
    private static final String FIELD_OBJECT_FIELDS = "fields";


    // Actions
    private static final String FIELD_FIELD = "field";
    private static final String ACTION_GET = "get";

    /**
     * Handler to interact with JSON nodes, should match what is expected for mapping files later in the process
     */
    private final INodeHandler<T, O, A> handler;

    public MapperLoader<T, O, A> loadDefaults() {
        IntegerMapper.register(this);
        DoubleMapper.register(this);
        StringMapper.register(this);
        AsIsMapper.register(this);
        ArraySplitMapper.register(this);
        ArrayJoinMapper.register(this);
        return this;
    }

    public void addMapper(String type, IMapperFunc<T, O, A> mapper) {
        this.mappers.put(type, mapper);
    }

    public void addMapperBuilder(String type, BiFunction<MapperLoader<T, O, A>, O, IMapperFunc<T, O, A>> builder) {
        this.mapperBuilders.put(type, builder);
    }

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
            if (!handler.hasField(objectNode, FIELD_MAPPER)) {
                throw new IllegalArgumentException("mapper configuration is missing 'mapper' field");
            }
            final String key = handler.asText(handler.getField(objectNode, FIELD_KEY));
            final T mapping = handler.getField(objectNode, FIELD_MAPPER);
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

    public MapperNode<T, O, A> loadNode(O mappingObject) {
        final String type = handler.asText(handler.getField(mappingObject, FIELD_TYPE));
        final String fieldName = Optional.ofNullable(handler.getField(mappingObject, FIELD_KEY)).map(handler::asText).orElse(null);

        final T accessor = handler.getField(mappingObject, FIELD_ACCESSOR);
        final IAccessorFunc<T> accessorFunc = loadAccessor(accessor);

        // Handles object creation
        if(type.equalsIgnoreCase(TYPE_OBJECT)) {
            final MapperNodeObject<T, O, A> objectNode = new MapperNodeObject();
            objectNode.setAccessor(accessorFunc);
            objectNode.setFieldName(fieldName);

            // Load pre-processor if provided
            if(handler.hasField(mappingObject, FIELD_MAPPER)) {
                final O nestedMapper = handler.asObject(handler.getField(mappingObject, FIELD_MAPPER));
                final String nestedType = handler.asText(handler.getField(nestedMapper, FIELD_TYPE));
                objectNode.setMapper(loadMapper(nestedType, nestedMapper));
            }

            // Load fields, required
            A fields = handler.asArray(handler.getField(mappingObject, FIELD_OBJECT_FIELDS));
            objectNode.setFields(handler.asStream(fields, false).map(handler::asObject).map(this::loadNode).toList());

            return objectNode;
        }
        // Handles array creation
        else if(type.equalsIgnoreCase(TYPE_ARRAY)) {
            final MapperNodeArray<T, O, A> arrayNode = new MapperNodeArray();
            arrayNode.setAccessor(accessorFunc);
            arrayNode.setFieldName(fieldName);

            // Load pre-processor if provided
            if(handler.hasField(mappingObject, FIELD_MAPPER)) {
                final O nestedMapper = handler.asObject(handler.getField(mappingObject, FIELD_MAPPER));
                final String nestedType = handler.asText(handler.getField(nestedMapper, FIELD_TYPE));
                arrayNode.setMapper(loadMapper(nestedType, nestedMapper));
            }

            // Load item mapper, required
            final O itemMapper = handler.asObject(handler.getField(mappingObject, FIELD_ARRAY_ITEM));
            final String nestedType = handler.asText(handler.getField(itemMapper, FIELD_TYPE));
            arrayNode.setMapper(loadMapper(nestedType, itemMapper));

            return arrayNode;
        }

        // Handles fields on objects
        final MapperNode<T, O, A> mapperNode = new MapperNode();
        mapperNode.setFieldName(fieldName);
        mapperNode.setAccessor(accessorFunc);
        mapperNode.setMapper(loadMapper(type, mappingObject));
        return mapperNode;
    }

    public IMapperFunc<T, O, A> loadMapper(String type, O mappingObject) {

        // Pull from map
        IMapperFunc<T, O, A> mapper = mappers.get(type);

        // If simple mappers don't work then try the builders
        if(mapper == null) {
            BiFunction<MapperLoader<T, O, A>, O, IMapperFunc<T, O, A>> builder = mapperBuilders.get(type);
            if(builder != null) {
               mapper = builder.apply(this, mappingObject);
            }
        }

        // Return mapper if found
        if(mapper != null) {
            return mapper;
        }
        //TODO add a string template that converts fields into a string EX: "${id}-${nameShort}-${nameFull}"
        //TODO add a way to create an array from objects that are not the same type
        //TODO add date converter, use built in date-time parser to take an input and output format as string
        //TODO add a toString with formatter for cases like "100.3567" -> "$100.36"

        throw new IllegalArgumentException("mapper configuration failed to load unknown mapper type; node=" + mappingObject);
    }

    public IAccessorFunc<T> loadAccessor(T accessorNode) {

        // Series of step to run, taking input from the previous step
        if(handler.isArray(accessorNode)) {
            final List<IAccessorFunc<T>> accessors = handler.asStream(handler.asArray(accessorNode), false).map(this::loadAccessor).toList();
            return (node, root) -> {
                T value = node; 
                for(IAccessorFunc<T> nextAccessor : accessors) {
                    value = nextAccessor.apply(value, root);
                }
                return value;
            };
        }
        // More complex accessors, usually meaning we are pulling data for arrays or flatMapping
        else if(handler.isObject(accessorNode)) {
            final O accessorObject = handler.asObject(accessorNode);
            final String action = handler.asText(handler.getField(accessorObject, FIELD_TYPE));
            final boolean useRoot = Optional.ofNullable(handler.getField(accessorObject, "root")).map(handler::asBoolean).orElse(false);

            // Simple get field type
            if(action.equalsIgnoreCase(ACTION_GET)) {
                return new AccessorGet(handler, handler.asText(handler.getField(accessorObject, FIELD_FIELD)), useRoot);
            }
            else {
                throw new IllegalArgumentException("mapper configuration failed to load unknown action type; node=" + accessorNode);
            }
        }
        return new AccessorGet(handler, handler.asText(accessorNode), false);
    }
}
