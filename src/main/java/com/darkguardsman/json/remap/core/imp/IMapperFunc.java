package com.darkguardsman.json.remap.core.imp;

@FunctionalInterface
public interface IMapperFunc<T extends Object, O extends T, A extends T> {

    /**
     * Called to convert the input node into an output node
     *
     * @param handler abstracting the json interaction
     * @param input to convert
     * @return output
     */
    T apply(INodeHandler<T, O, A> handler, T input);
}
