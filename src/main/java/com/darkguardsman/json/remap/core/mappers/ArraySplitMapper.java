package com.darkguardsman.json.remap.core.mappers;

import com.darkguardsman.json.remap.core.MapperLoader;
import com.darkguardsman.json.remap.core.imp.IMapperFunc;
import com.darkguardsman.json.remap.core.imp.INodeHandler;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;

/**
 * Converts input node into an string node
 */
@AllArgsConstructor
public class ArraySplitMapper<T, O extends T, A extends T> implements IMapperFunc<T, O, A> {

    public static final String TYPE_ARRAY_SPLIT = "array.split";

    public static <T, O extends T, A extends T> void register(MapperLoader<T, O, A> loader) {
        loader.addMapperBuilder(TYPE_ARRAY_SPLIT, ArraySplitMapper::build);
    }

    public static <T, O extends T, A extends T> ArraySplitMapper<T, O, A> build(MapperLoader<T, O, A> loader, O source) {
       final String splitter = loader.getHandler().asText(loader.getHandler().getField(source, "splitter"));
       return new ArraySplitMapper(splitter);
    }

    private final String splitString;

    @Override
    public T apply(INodeHandler<T, O, A> factory, T input) {
        final String[] split = factory.asText(input).split(splitString);
        final A array = factory.newArray();
        for(String s : split) {
            factory.addItem(array, factory.newText(s));
        }
        return array;
    }
}
