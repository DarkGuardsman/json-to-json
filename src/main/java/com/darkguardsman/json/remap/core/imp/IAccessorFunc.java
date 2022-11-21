package com.darkguardsman.json.remap.core.imp;

/**
 * Functional interface used to access & in some cases transform the input node into
 * something usable by the mapper.
 * <p>
 * In most cases this will be a direct field access. Such as pulling a field from an object or
 * getting the nth element in an array.
 * <p>
 * In rare cases it may be desired to quickly transform the data. Making this act more like a
 * pre-processor or mapper in itself. Often this will be cases when we need a single field from
 * an object or need to convert nested arrays into a flattened array.
 *
 * @param <T> json node
 */
@FunctionalInterface
public interface IAccessorFunc<T> {

    /**
     * Used to access/transform input value into usable value for mappers
     *
     * @param node to access value form
     * @param root for cases when we need something above node
     * @return node pulled from input(s)
     */
    T apply(T node, T root);
}
