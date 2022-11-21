package com.darkguardsman.json.remap.core.imp;

@FunctionalInterface
public interface IMapperNode<T extends Object, O extends T, A extends T> {

    /**
     * Called to process the current mapper node
     *
     * @param handler abstracting the json interaction
     * @param parentNode accessed previous or rootNode if first mapper
     * @param rootNode for cases when we need a few levels above the parent
     * @return mapped node
     */
    T apply(INodeHandler<T, O, A> handler, T parentNode, T rootNode);
}
