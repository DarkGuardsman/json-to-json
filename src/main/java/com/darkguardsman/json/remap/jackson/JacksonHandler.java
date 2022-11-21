package com.darkguardsman.json.remap.jackson;

import com.darkguardsman.json.remap.core.MapperNode;
import com.darkguardsman.json.remap.core.NodeHandler;
import com.darkguardsman.json.remap.core.errors.MapperException;
import com.darkguardsman.json.remap.core.errors.MapperTypeException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import lombok.AllArgsConstructor;

import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@AllArgsConstructor
public class JacksonHandler implements NodeHandler<JsonNode, ObjectNode, ArrayNode> {

    private final ObjectMapper mapper;

    @Override
    public ObjectNode newObject() {
        return mapper.getNodeFactory().objectNode();
    }

    @Override
    public ArrayNode newArray() {
        return mapper.getNodeFactory().arrayNode();
    }

    @Override
    public NullNode newNull() {
        return mapper.getNodeFactory().nullNode();
    }

    @Override
    public NumericNode newInteger(int num) {
        return mapper.getNodeFactory().numberNode(num);
    }

    @Override
    public NumericNode newFloating(double num) {
        return mapper.getNodeFactory().numberNode(num);
    }

    @Override
    public TextNode newText(String text) {
        return mapper.getNodeFactory().textNode(text);
    }

    @Override
    public String asText(JsonNode node) {
        return node.asText();
    }

    @Override
    public int asInt(JsonNode node) {
        return node.asInt();
    }

    @Override
    public double asDouble(JsonNode node) {
        return node.asDouble();
    }

    @Override
    public ObjectNode asObject(JsonNode node) {
        assert Objects.nonNull(node);
        if(node instanceof ObjectNode) {
            return (ObjectNode) node;
        }
       throw new MapperTypeException("Node is not an object, node=" + node.getNodeType());
    }

    @Override
    public ArrayNode asArray(JsonNode node) {
        assert Objects.nonNull(node);
        if(node instanceof ArrayNode) {
            return (ArrayNode) node;
        }
        throw new MapperTypeException("Node is not an array, node=" + node.getNodeType());
    }

    @Override
    public Stream<JsonNode> asStream(ArrayNode array, boolean parallel) {
        return StreamSupport.stream(array.spliterator(), parallel);
    }

    @Override
    public boolean isObject(JsonNode node) {
        return node instanceof ObjectNode;
    }

    @Override
    public boolean isArray(JsonNode node) {
        return node instanceof ArrayNode;
    }

    @Override
    public void setField(ObjectNode object, String field, JsonNode node) {
        object.set(field, node);
    }

    @Override
    public JsonNode getField(ObjectNode object, String field) {
        return object.get(field);
    }

    @Override
    public boolean hasField(ObjectNode object, String field) {
        return object.has(field);
    }
}
