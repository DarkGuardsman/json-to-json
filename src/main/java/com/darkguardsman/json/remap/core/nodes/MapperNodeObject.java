package com.darkguardsman.json.remap.core.nodes;

import com.darkguardsman.json.remap.core.errors.MapperException;
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
public class MapperNodeObject<T, O extends T, A extends T> extends MapperNode<T, O, A> {

    private List<MapperNode<T, O, A>> fields;

    @Override
    public T apply(INodeHandler<T, O, A> factory, T node, T root) {
        // Get value using accessor
        T value = super.apply(factory, node, root);

        // Create new object
        O objectNode = factory.newObject();

        // Convert value if we have a mapper
        fields.forEach(mapperNode -> {
            T fieldValue = mapperNode.apply(factory, value, root);
            if (mapperNode.getFieldName() == null || mapperNode.getFieldName().isBlank()) {
                throw new MapperException("Missing field name while mapping object; mapper=" + this);
            }
            factory.setField(objectNode, mapperNode.getFieldName(), fieldValue);
        });

        return objectNode;
    }
}
