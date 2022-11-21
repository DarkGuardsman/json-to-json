package com.darkguardsman.json.remap.core.nodes;

import com.darkguardsman.json.remap.core.imp.IAccessorFunc;
import com.darkguardsman.json.remap.core.imp.IMapperFunc;
import com.darkguardsman.json.remap.core.imp.IMapperNode;
import com.darkguardsman.json.remap.core.imp.INodeHandler;
import lombok.Data;

import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Simple node to go from input -> output, often used for setting fields
 */
@Data
public class MapperNode<T, O extends T, A extends T> implements IMapperNode<T, O, A> {

    /**
     * Name of the field, only needed when assigning values to an object
     */
    private String fieldName;

    /**
     * Input json node to output json node
     */
    private IAccessorFunc<T> accessor;

    /**
     * Accessed data to mapper output, could be a simple mapper or nested mapper chain
     */
    private IMapperFunc<T, O, A> mapper;

    @Override
    public T apply(INodeHandler<T, O, A> factory, T node, T root) {
        // Get value using accessor
        T value = Optional.ofNullable(this.accessor).orElse((n, r) -> n).apply(node, root);

        // Convert value if we have a mapper
        if (this.mapper != null) {
            return this.mapper.apply(factory, value);
        }

        return value;
    }
}
