package com.darkguardsman.json.remap.core.mappers;

import com.darkguardsman.json.remap.core.MapperLoader;
import com.darkguardsman.json.remap.core.imp.INodeHandler;
import com.darkguardsman.json.remap.core.imp.IMapperFunc;

/**
 * Converts input node into an integer node
 */
public class IntegerMapper<T extends Object, O extends T, A extends T> implements IMapperFunc<T, O, A> {

    private static final String TYPE_INT = "int";
    private static final String TYPE_INTEGER = "integer";

    public static <T, O extends T, A extends T> void register(MapperLoader<T, O, A> loader) {
        final IntegerMapper<T, O, A> mapper = new IntegerMapper();
        loader.addMapper(TYPE_INT, mapper);
        loader.addMapper(TYPE_INTEGER, mapper);
    }

    @Override
    public T apply(INodeHandler<T, O, A> factory, T input) {
        return factory.newInteger(factory.asInt(input));
    }
}
