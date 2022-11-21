package com.darkguardsman.json.remap.core;

import com.darkguardsman.json.remap.core.imp.INodeHandler;
import com.darkguardsman.json.remap.core.nodes.MapperNode;
import lombok.Data;

@Data
public class MapperData<T extends Object, O extends T, A extends T> {

    /** Unique key for this mapper, ex: com.darkguardsman.event.messages:post:3.0.1 */
    private String key;

    /** Node that accepts the root json to start the mapper */
    private MapperNode<T, O, A> rootNode;

    /**
     * Invokes to remap a node into a new json node
     *
     * @param factory used to create new nodes
     * @param node to transverse
     * @return new json created from the original node
     */
    public T handle(INodeHandler<T, O, A> factory, T node) {
        return rootNode.apply(factory, node, node);
    }
}
