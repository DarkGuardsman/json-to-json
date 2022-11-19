package com.darkguardsman.json.remap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MapperNodeTest {

    @Test
    void remapObject() {

        final ObjectMapper objectMapper = new ObjectMapper();

        // Setup input object
        final ObjectNode inputObject = objectMapper.createObjectNode();
        inputObject.put("field1", 123);
        inputObject.put("field2", "cat");
        inputObject.set("field3", objectMapper.createArrayNode().add(1).add(2).add(3));

        // Setup input object
        final ObjectNode expectedOutput = objectMapper.createObjectNode();
        expectedOutput.set("type", objectMapper.createObjectNode()
                .put("id", 123)
                .put("name", "cat")
        );
        expectedOutput.set("values", objectMapper.createArrayNode()
                .add(objectMapper.createObjectNode().put("id", 1))
                .add(objectMapper.createObjectNode().put("id", 2))
                .add(objectMapper.createObjectNode().put("id", 3))
        );

        // Setup mapper nodes
        final MapperNode firstField = new MapperNode();
        firstField.setFieldName("type");
        firstField.setMapper((mapper, node) -> {
            final ObjectNode objectNode = mapper.createObjectNode();
            objectNode.set("id", node.get("field1"));
            objectNode.set("name", node.get("field2"));
            return objectNode;
        });
        final MapperNode secondField = new MapperNode();
        secondField.setFieldName("values");
        secondField.setAccessor((node) -> node.get("field3"));
        secondField.setMapper((mapper, node) -> {
            if (node instanceof ArrayNode sourceArray) {
                final ArrayNode arrayNode = mapper.createArrayNode();
                sourceArray.forEach(value -> {
                    arrayNode.add(mapper.createObjectNode().put("id", value.asInt()));
                });
                return arrayNode;
            }
            return mapper.nullNode();
        });
        final MapperNode rootNode = new MapperNode();
        rootNode.setMapper(List.of(
                firstField,
                secondField
        ));

        // Invoke remapping
        final JsonNode output = rootNode.apply(objectMapper, inputObject);

        // Check they match expected
        Assertions.assertEquals(expectedOutput, output);
    }
}
