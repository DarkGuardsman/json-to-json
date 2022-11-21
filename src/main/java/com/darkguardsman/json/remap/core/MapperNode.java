package com.darkguardsman.json.remap.core;

import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@Data
public class MapperNode<T extends Object, O extends T, A extends T> implements BiFunction<NodeHandler<T, O, A>, T, T> {

    /** Parent node */
    private MapperNode<T, O, A> parent;

    /** Name of the field, only needed when assigning values to an object */
    private String fieldName;

    /** Input json node to output json node */
    private Function<T, T> accessor;

    /** Accessed data to mapper output, could be a simple mapper or nested mapper chain */
    private BiFunction<NodeHandler<T, O, A>, T, T> mapper;

    public MapperNode<T, O, A> setMapper(BiFunction<NodeHandler<T, O, A>, T, T> node) {
        this.mapper = node;
        return this;
    }

    /**
     * Called to apply node for remap
     *
     * @param node to remap, if first mapper this will be JSON root
     * @return updated value
     */
    public T apply(NodeHandler<T, O, A> objectMapper, T node) {
        //Use accessor if present, if not try to use parent's accessor, it no parent accessor then use node passed in as this is likely root
       final T value = Optional.ofNullable(accessor).orElse(Optional.ofNullable(parent).map(MapperNode::getAccessor).orElse((n) -> n)).apply(node);
       if(mapper != null) {
           return mapper.apply(objectMapper, value);
       }
       return value;
    }
}
