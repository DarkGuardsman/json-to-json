package com.darkguardsman.json.remap.mappers;

import com.darkguardsman.json.remap.core.MapperLoader;
import com.darkguardsman.json.remap.core.imp.INodeHandler;
import com.darkguardsman.json.remap.core.mappers.ArrayJoinMapper;
import com.darkguardsman.json.remap.core.mappers.AsIsMapper;
import com.darkguardsman.json.remap.fakes.FakeStringNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ArrayJoinMapperTest {

    @Test
    @DisplayName("Verify we default ',' for joiner if null")
    void build_default() {
        final INodeHandler handler = Mockito.mock(INodeHandler.class);
        final MapperLoader loader = new MapperLoader(handler);

        // Using a map as an object node
        final Map<String, Object> fakeNode = new HashMap();

        // Mock field return
        Mockito.when(handler.getField(fakeNode, "joiner")).thenReturn(null);

        // Build mapper
        ArrayJoinMapper newMapper = ArrayJoinMapper.build(loader, fakeNode);

        // Validate our starting settings
        Assertions.assertEquals(",", newMapper.getJoiner());
    }

    @Test
    @DisplayName("Verify joiner is set to ';' from loading object")
    void build() {
        final INodeHandler handler = Mockito.mock(INodeHandler.class);
        final MapperLoader loader = new MapperLoader(handler);

        // Using a map as an object node
        final Map<String, Object> fakeNode = new HashMap();
        final FakeStringNode textNode = new FakeStringNode(";");

        // Mock field return
        Mockito.when(handler.getField(fakeNode, "joiner")).thenReturn(textNode);
        Mockito.when(handler.asText(textNode)).thenReturn(";");

        // Build mapper
        ArrayJoinMapper newMapper = ArrayJoinMapper.build(loader, fakeNode);

        // Validate our starting settings
        Assertions.assertEquals(";", newMapper.getJoiner());
    }

    @Test
    @DisplayName("Joins array [cat,dog,bird] into 'cat, dog, bird' ")
    void joinsStrings() {

        // Setup data
        final FakeStringNode[] array = new FakeStringNode[]{new FakeStringNode("cat"), new FakeStringNode("dog"), new FakeStringNode("bird")};
        final Stream<FakeStringNode> nodeStream = Arrays.stream(array);

        // Mock handler
        final INodeHandler handler = Mockito.mock(INodeHandler.class);
        Mockito.when(handler.asArray(array)).thenReturn(array);
        Mockito.when(handler.asStream(array, false)).thenReturn(nodeStream);
        Mockito.when(handler.asText(Mockito.any())).thenAnswer((arg) -> {
            return ((FakeStringNode)arg.getArgument(0)).getText();
        });
        Mockito.when(handler.newText(Mockito.any())).thenAnswer((arg) -> {
            return new FakeStringNode(arg.getArgument(0));
        });

        // Setup and invoke mapper
        final ArrayJoinMapper mapper = new ArrayJoinMapper(", ");
        FakeStringNode node = (FakeStringNode) mapper.apply(handler, array);

        // Check that we merged strings
        Assertions.assertEquals("cat, dog, bird", node.getText());
    }

    @Test
    @DisplayName("Handles empty array as null")
    void joinsEmptyArray() {

        // Setup data
        final FakeStringNode[] array = new FakeStringNode[]{};

        // Mock handler
        final INodeHandler handler = Mockito.mock(INodeHandler.class);
        Mockito.when(handler.asArray(array)).thenReturn(array);
        Mockito.when(handler.isEmpty(array)).thenReturn(true);
        Mockito.when(handler.newNull()).thenReturn(null);

        // Setup and invoke mapper
        final ArrayJoinMapper mapper = new ArrayJoinMapper(", ");
        FakeStringNode node = (FakeStringNode) mapper.apply(handler, array);

        // Check that we merged strings
        Assertions.assertNull(node);
    }

    @Test
    @DisplayName("Handles empty array as null")
    void joinsNullArray() {

        // Setup data
        final FakeStringNode[] array = new FakeStringNode[]{};

        // Mock handler
        final INodeHandler handler = Mockito.mock(INodeHandler.class);
        Mockito.when(handler.isNull(array)).thenReturn(true);
        Mockito.when(handler.newNull()).thenReturn(null);

        // Setup and invoke mapper
        final ArrayJoinMapper mapper = new ArrayJoinMapper(", ");
        FakeStringNode node = (FakeStringNode) mapper.apply(handler, array);

        // Check that we merged strings
        Assertions.assertNull(node);
    }
}
