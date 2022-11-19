package com.darkguardsman.json.remap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

@Data
public class MapperData {
    private MapperNode rootNode;

    public JsonNode handle(ObjectMapper objectMapper, JsonNode node) {
        return rootNode.apply(objectMapper, node);
    }
}
