package com.darkguardsman.json.remap;

import com.darkguardsman.json.remap.core.MapperNode;
import com.darkguardsman.json.remap.jackson.JacksonHandler;
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
        final JacksonHandler handler = new JacksonHandler(objectMapper);

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
        final MapperNode<JsonNode, ObjectNode, ArrayNode> firstField = new MapperNode();
        firstField.setFieldName("type");
        firstField.setMapper((mapper, node) -> {
            final ObjectNode objectNode = mapper.newObject();
            objectNode.set("id", node.get("field1"));
            objectNode.set("name", node.get("field2"));
            return objectNode;
        });
        final MapperNode<JsonNode, ObjectNode, ArrayNode> secondField = new MapperNode();
        secondField.setFieldName("values");
        secondField.setAccessor((node) -> node.get("field3"));
        secondField.setMapper((mapper, node) -> {
            if (node instanceof ArrayNode sourceArray) {
                final ArrayNode arrayNode = mapper.newArray();
                sourceArray.forEach(value -> {
                    final ObjectNode subObject = mapper.newObject();
                    mapper.setField(subObject, "id", handler.newInteger(value.asInt()));
                    arrayNode.add(subObject);
                });
                return arrayNode;
            }
            return mapper.newNull();
        });
        final MapperNode<JsonNode, ObjectNode, ArrayNode> rootNode = new MapperNode();
        rootNode.setMapper(List.of(
                firstField,
                secondField
        ));

        // Invoke remapping
        final JsonNode output = rootNode.apply(handler, inputObject);

        // Check they match expected
        Assertions.assertEquals(expectedOutput, output);
    }
}
