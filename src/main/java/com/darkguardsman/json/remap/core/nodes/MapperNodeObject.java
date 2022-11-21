package com.darkguardsman.json.remap.core.nodes;

import com.darkguardsman.json.remap.core.imp.INodeHandler;
import lombok.Data;

import java.util.List;

/**
 * Simple node to go from input -> output, often used for setting fields
 */
@Data
public class MapperNodeObject<T, O extends T, A extends T> extends MapperNode<T, O, A> {

    private List<MapperNode<T, O, A>> fields;

    @Override
    public T apply(INodeHandler<T, O, A> factory, T node, T root) {
        // Get value using accessor
        T value = super.apply(factory, node, root);

        // Create new object
        O objectNode = factory.newObject();

        // Convert value if we have a mapper
        if (this.fields != null && !fields.isEmpty()) {
           fields.forEach(mapperNode -> {
               T fieldValue = mapperNode.apply(factory, value, root);
               factory.setField(objectNode, mapperNode.getFieldName(), fieldValue);
           });
        }

        return objectNode;
    }
}
