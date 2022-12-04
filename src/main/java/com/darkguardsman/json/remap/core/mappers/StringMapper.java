package com.darkguardsman.json.remap.core.mappers;

import com.darkguardsman.json.remap.core.MapperLoader;
import com.darkguardsman.json.remap.core.imp.INodeHandler;
import com.darkguardsman.json.remap.core.imp.IMapperFunc;

/**
 * Converts input node into a string node
 */
public class StringMapper<T extends Object, O extends T, A extends T> implements IMapperFunc<T, O, A> {

    private static final String TYPE_STRING = "string";

    public static <T, O extends T, A extends T> void register(MapperLoader<T, O, A> loader) {
        loader.addMapper(TYPE_STRING, new StringMapper<T, O, A>());
    }

    @Override
    public T apply(INodeHandler<T, O, A> factory, T input) {
        return factory.newText(factory.asText(input));
    }
}
