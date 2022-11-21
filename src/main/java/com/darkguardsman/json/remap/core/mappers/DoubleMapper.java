package com.darkguardsman.json.remap.core.mappers;

import com.darkguardsman.json.remap.core.MapperLoader;
import com.darkguardsman.json.remap.core.imp.INodeHandler;
import com.darkguardsman.json.remap.core.imp.IMapperFunc;

/**
 * Converts input node into an double node
 */
public class DoubleMapper<T extends Object, O extends T, A extends T> implements IMapperFunc<T, O, A> {

    private static final String TYPE_DOUBLE = "double";

    public static <T, O extends T, A extends T> void register(MapperLoader<T, O, A> loader) {
        loader.addMapper(TYPE_DOUBLE, new DoubleMapper<T, O, A>());
    }

    @Override
    public T apply(INodeHandler<T, O, A> factory, T input) {
        return factory.newFloating(factory.asDouble(input));
    }
}
