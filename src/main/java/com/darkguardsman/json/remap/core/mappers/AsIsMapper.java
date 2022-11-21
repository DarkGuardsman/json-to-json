package com.darkguardsman.json.remap.core.mappers;

import com.darkguardsman.json.remap.core.MapperLoader;
import com.darkguardsman.json.remap.core.imp.INodeHandler;
import com.darkguardsman.json.remap.core.imp.IMapperFunc;

/**
 * Pass through mapper, useful for when the existing JSON is already in the correct format for the given field
 */
public class AsIsMapper<T extends Object, O extends T, A extends T> implements IMapperFunc<T, O, A> {

    private static final String TYPE_AS_IS = "current";

    public static <T, O extends T, A extends T> void register(MapperLoader<T, O, A> loader) {
        loader.addMapper(TYPE_AS_IS, new AsIsMapper<T, O, A>());
    }

    @Override
    public T apply(INodeHandler<T, O, A> factory, T input) {
        return input;
    }
}
