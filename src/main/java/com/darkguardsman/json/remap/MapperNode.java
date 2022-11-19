package com.darkguardsman.json.remap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@Data
public class MapperNode implements BiFunction<ObjectMapper, JsonNode, JsonNode> {

    /** Parent node */
    private MapperNode parent;

    /** Name of the field, only needed when assigning values to an object */
    private String fieldName;

    /** Input json node to output json node */
    private Function<JsonNode, JsonNode> accessor;

    /** Accessed data to mapper output, could be a simple mapper or nested mapper chain */
    private BiFunction<ObjectMapper, JsonNode, JsonNode> mapper;

    public MapperNode setMapper(BiFunction<ObjectMapper, JsonNode, JsonNode> node) {
        this.mapper = node;
        return this;
    }

    public MapperNode setMapper(List<MapperNode> nodes) {
        this.mapper = (objectMapper, value) -> {
            final ObjectNode objectNode = objectMapper.createObjectNode();
            nodes.forEach(node -> {
                objectNode.set(node.fieldName, node.apply(objectMapper, value));
            });
            return objectNode;
        };
        return this;
    }

    /**
     * Called to apply node for remap
     *
     * @param node to remap, if first mapper this will be JSON root
     * @return updated value
     */
    public JsonNode apply(ObjectMapper objectMapper, JsonNode node) {
        //Use accessor if present, if not try to use parent's accessor, it no parent accessor then use node passed in as this is likely root
       final JsonNode value = Optional.ofNullable(accessor).orElse(Optional.ofNullable(parent).map(MapperNode::getAccessor).orElse((n) -> n)).apply(node);
       if(mapper != null) {
           return mapper.apply(objectMapper, value);
       }
       return value;
    }
}
