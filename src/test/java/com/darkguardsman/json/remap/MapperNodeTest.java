package com.darkguardsman.json.remap;

import com.darkguardsman.json.remap.core.nodes.MapperNode;
import com.darkguardsman.json.remap.core.nodes.MapperNodeObject;
import com.darkguardsman.json.remap.jackson.JacksonHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MapperNodeTest {

    final ObjectMapper objectMapper = new ObjectMapper();
    final JacksonHandler handler = new JacksonHandler(objectMapper);

    @Test
    void remapObject() {

       final ObjectNode inputObject = createInput();
       final ObjectNode expectedOutput = createOutput();

        // Convert category into sub-object
        final MapperNodeObject<JsonNode, ObjectNode, ArrayNode> firstField = new MapperNodeObject();
        firstField.setFieldName("category");

        final MapperNode<JsonNode, ObjectNode, ArrayNode> typeId = new MapperNode();
        typeId.setFieldName("id");
        typeId.setAccessor((node, root) -> node.get("categoryId"));

        final MapperNode<JsonNode, ObjectNode, ArrayNode> typeName = new MapperNode();
        typeName.setFieldName("name");
        typeName.setAccessor((node, root) -> node.get("categoryName"));

        firstField.setFields(List.of(typeId, typeName));

        // Convert value array into object array
        final MapperNode<JsonNode, ObjectNode, ArrayNode> secondField = new MapperNode();
        secondField.setFieldName("options");
        secondField.setAccessor((node, root) -> node.get("values"));
        secondField.setMapper((mapper, node) -> {
            if (node instanceof ArrayNode sourceArray) {
                final ArrayNode arrayNode = mapper.newArray();
                sourceArray.forEach(value -> {
                    final String[] split = value.asText().split(":");
                    final ObjectNode subObject = mapper.newObject();
                    mapper.setField(subObject, "id", handler.newInteger(Integer.parseInt(split[0])));
                    mapper.setField(subObject, "name", handler.newText(split[1]));
                    arrayNode.add(subObject);
                });
                return arrayNode;
            }
            return mapper.newNull();
        });

        // Simulate converting "data" to new object
        final MapperNodeObject<JsonNode, ObjectNode, ArrayNode> rootNode = new MapperNodeObject();
        rootNode.setAccessor((node, root) -> node.get("data"));
        rootNode.setFields(List.of(firstField, secondField));

        // Invoke remapping
        final JsonNode output = rootNode.apply(handler, inputObject, inputObject);

        // Check they match expected
        Assertions.assertEquals(expectedOutput, output);
    }

    private ObjectNode createInput() {
        // Setup input object
        final ObjectNode inputObject = objectMapper.createObjectNode();

        final ObjectNode dataObject = objectMapper.createObjectNode();
        dataObject.put("categoryId", 123);
        dataObject.put("categoryName", "Cat Size");
        dataObject.set("values", objectMapper.createArrayNode().add("1:small").add("2:medium").add("3:large"));

        inputObject.set("data", dataObject);

        return inputObject;
    }

    private ObjectNode createOutput() {
        final ObjectNode expectedOutput = objectMapper.createObjectNode();
        expectedOutput.set("category", objectMapper.createObjectNode()
                .put("id", 123)
                .put("name", "Cat Size")
        );
        expectedOutput.set("options", objectMapper.createArrayNode()
                .add(objectMapper.createObjectNode().put("id", 1).put("name", "small"))
                .add(objectMapper.createObjectNode().put("id", 2).put("name", "medium"))
                .add(objectMapper.createObjectNode().put("id", 3).put("name", "large"))
        );
        return expectedOutput;
    }
}
