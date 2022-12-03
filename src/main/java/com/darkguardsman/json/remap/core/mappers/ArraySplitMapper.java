package com.darkguardsman.json.remap.core.mappers;

import com.darkguardsman.json.remap.core.MapperLoader;
import com.darkguardsman.json.remap.core.imp.IMapperFunc;
import com.darkguardsman.json.remap.core.imp.INodeHandler;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * Converts input node into an string node
 */
@AllArgsConstructor
public class ArraySplitMapper<T, O extends T, A extends T> implements IMapperFunc<T, O, A> {

    public static final String TYPE_ARRAY_SPLIT = "array.split";

    private static final String FIELD_SPLITTER = "splitter";
    private static final String FIELD_TRIM = "trim";

    private static final String SPLITTER_DEFAULT = ",";

    public static <T, O extends T, A extends T> void register(MapperLoader<T, O, A> loader) {
        loader.addMapperBuilder(TYPE_ARRAY_SPLIT, ArraySplitMapper::build);
    }

    public static <T, O extends T, A extends T> ArraySplitMapper<T, O, A> build(MapperLoader<T, O, A> loader, O source) {
        final INodeHandler<T, O, A> handler = loader.getHandler();
        final String splitter = Optional.ofNullable(handler.getField(source, FIELD_SPLITTER)).map(handler::asText).orElse(SPLITTER_DEFAULT);
        final boolean doTrim = Optional.ofNullable(handler.getField(source, FIELD_TRIM)).map(handler::asBoolean).orElse(false);
        return new ArraySplitMapper(splitter, doTrim);
    }

    @Getter
    private final String splitString;
    @Getter
    private final boolean doTrim;

    @Override
    public T apply(INodeHandler<T, O, A> factory, T input) {
        // If input is null then return null node
        if (factory.isNull(input)) {
            return factory.newNull();
        }
        // If input is empty then return empty array
        if (factory.isEmpty(input)) {
            return factory.newArray();
        }

        // Split string and turn into array
        final String[] split = factory.asText(input).split(splitString);
        final A array = factory.newArray();
        for (String s : split) {
            factory.addItem(array, factory.newText(doTrim ? s.trim() : s));
        }
        return array;
    }
}
