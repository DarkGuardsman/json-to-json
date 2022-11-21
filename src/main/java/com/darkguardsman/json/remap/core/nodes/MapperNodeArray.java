package com.darkguardsman.json.remap.core.nodes;

import com.darkguardsman.json.remap.core.imp.INodeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Simple node to go from input -> output, often used for setting fields
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MapperNodeArray<T, O extends T, A extends T> extends MapperNode<T, O, A> {

    private MapperNode<T, O, A> itemMapper;

    @Override
    public T apply(INodeHandler<T, O, A> factory, T node, T root) {
        // Get value using accessor
        A array = factory.asArray(super.apply(factory, node, root));

        // Create new array
        A newArray = factory.newArray();

        // Map each item
        factory.asStream(array, false).forEach(arrayNode -> {
            factory.addItem(newArray, itemMapper.apply(factory, arrayNode, root));
        });

        return newArray;
    }
}
