package com.darkguardsman.json.remap;

import com.darkguardsman.json.remap.core.nodes.MapperNode;
import com.darkguardsman.json.remap.core.nodes.MapperNodeArray;
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

        // This is just an example for use in testing the concept. Normally developers would use the loader
        // to convert a JSON into mapper data for use.

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
        final MapperNodeArray<JsonNode, ObjectNode, ArrayNode> secondField = new MapperNodeArray();
        secondField.setFieldName("options");
        secondField.setAccessor((node, root) -> node.get("values"));

        // Build array item mapper
        final MapperNodeObject<JsonNode, ObjectNode, ArrayNode> itemMapper = new MapperNodeObject();
        secondField.setItemMapper(itemMapper);
        itemMapper.setMapper((mapper, node) -> {
            ArrayNode array = mapper.newArray();
            String[] split = node.asText().split(":");
            array.add(split[0]);
            array.add(split[1]);
            return array;
        });
        final MapperNode<JsonNode, ObjectNode, ArrayNode> valueId = new MapperNode();
        valueId.setFieldName("id");
        valueId.setMapper((handler, node) -> handler.newInteger(handler.asInt(node)));
        valueId.setAccessor((node, root) -> node.get(0));

        final MapperNode<JsonNode, ObjectNode, ArrayNode> valueName = new MapperNode();
        valueName.setFieldName("name");
        valueName.setAccessor((node, root) -> node.get(1));

        itemMapper.setFields(List.of(valueId, valueName));


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
