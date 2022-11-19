package com.darkguardsman.json.remap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

@Data
public class MapperData {

    /** Unique key for this mapper, ex: com.darkguardsman.event.messages:post:3.0.1 */
    private String key;

    /** Node that accepts the root json to start the mapper */
    private MapperNode rootNode;

    /**
     * Invokes to remap a node into a new json node
     *
     * @param objectMapper used to create new nodes
     * @param node to transverse
     * @return new json created from the original node
     */
    public JsonNode handle(ObjectMapper objectMapper, JsonNode node) {
        return rootNode.apply(objectMapper, node);
    }
}
