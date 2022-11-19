package com.darkguardsman.json.remap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Locale;
import java.util.function.Function;

public class MapperLoader {

    /**
     * Used to load a configuration file used to generate a mapper
     *
     * @param node containing the configuration file
     * @return root mapper node
     */
    public static MapperData loadMapper(JsonNode node) {
        if(node instanceof ObjectNode objectNode) {
            if(!objectNode.has("key")) {
                throw new IllegalArgumentException("mapper configuration is missing 'key' field");
            }
            if(!objectNode.has("mapping")) {
                throw new IllegalArgumentException("mapper configuration is missing 'mapping' field");
            }
            final String key = objectNode.get("key").asText();
            final JsonNode mapping = objectNode.get("mapping");
            if(mapping instanceof ObjectNode firstNode) {
                final MapperData data = new MapperData();
                data.setKey(key);
                data.setRootNode(loadNode(firstNode));
                return data;
            }
            throw new IllegalArgumentException("mapper configuration needs 'mapping' field to be an object");
        }
        throw new IllegalArgumentException("mapper configuration needs to be a JSON object");
    }

    private static MapperNode loadNode(ObjectNode node) {
        final String type = node.get("type").asText().toLowerCase(Locale.ROOT);
        final String accessor = node.get("accessor").asText();

        final MapperNode mapperNode = new MapperNode();
        mapperNode.setAccessor(loadAccessor(accessor));

        if(type.equalsIgnoreCase("string")) {
            mapperNode.setMapper(((objectMapper, input) -> objectMapper.valueToTree(input.asText())));
        }
        else if(type.equalsIgnoreCase("integer") || type.equalsIgnoreCase("int")) {
            mapperNode.setMapper(((objectMapper, input) -> objectMapper.valueToTree(input.asInt())));
        }
        else if(type.equalsIgnoreCase("double")) {
            mapperNode.setMapper(((objectMapper, input) -> objectMapper.valueToTree(input.asDouble())));
        }
        else {
            throw new IllegalArgumentException("mapper configuration failed to load unknown mapper type; node=" + node);
        }

        return mapperNode;
    }

    private static Function<JsonNode, JsonNode> loadAccessor(String accessor) {
        if(accessor != null) {
            String[] split = accessor.split("[.]");
            return (node) -> {
                JsonNode value = node;
                for(String field : split) {
                    value = node.get(field);
                    if(value == null) {
                        return null;
                    }
                }
                return value;
            };
        }
        return null;
    }
}
