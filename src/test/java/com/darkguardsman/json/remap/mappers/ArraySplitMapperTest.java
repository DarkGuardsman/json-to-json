package com.darkguardsman.json.remap.mappers;

import com.darkguardsman.json.remap.core.MapperLoader;
import com.darkguardsman.json.remap.core.imp.INodeHandler;
import com.darkguardsman.json.remap.core.mappers.ArrayJoinMapper;
import com.darkguardsman.json.remap.core.mappers.ArraySplitMapper;
import com.darkguardsman.json.remap.fakes.FakeBooleanNode;
import com.darkguardsman.json.remap.fakes.FakeStringNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArraySplitMapperTest {

    @Test
    @DisplayName("Verify we use defaults if nothing is specified")
    void build_default() {
        final INodeHandler handler = Mockito.mock(INodeHandler.class);
        final MapperLoader loader = new MapperLoader(handler);

        // Using a map as an object node
        final Map<String, Object> fakeNode = new HashMap();

        // Mock field return
        Mockito.when(handler.getField(fakeNode, "splitter")).thenReturn(null);
        Mockito.when(handler.getField(fakeNode, "trim")).thenReturn(null);

        // Build mapper
        ArraySplitMapper newMapper = ArraySplitMapper.build(loader, fakeNode);

        // Validate our starting settings
        Assertions.assertEquals(",", newMapper.getSplitString());
        Assertions.assertFalse(newMapper.isDoTrim());
    }

    @Test
    @DisplayName("Verify we use settings when specified")
    void build() {
        final INodeHandler handler = Mockito.mock(INodeHandler.class);
        final MapperLoader loader = new MapperLoader(handler);

        // Using a map as an object node
        final Map<String, Object> fakeNode = new HashMap();
        final FakeStringNode textNode = new FakeStringNode("; ");
        final FakeBooleanNode booleanNode = new FakeBooleanNode(true);

        // Mock field return
        Mockito.when(handler.getField(fakeNode, "splitter")).thenReturn(textNode);
        Mockito.when(handler.getField(fakeNode, "trim")).thenReturn(booleanNode);
        Mockito.when(handler.asText(textNode)).thenReturn("; ");
        Mockito.when(handler.asBoolean(booleanNode)).thenReturn(true);

        // Build mapper
        ArraySplitMapper newMapper = ArraySplitMapper.build(loader, fakeNode);

        // Validate our starting settings
        Assertions.assertEquals("; ", newMapper.getSplitString());
        Assertions.assertTrue(newMapper.isDoTrim());
    }

    @Test
    @DisplayName("Turns empty string into empty array")
    void handleEmptyString() {

        // Setup data
        final FakeStringNode textNode = new FakeStringNode("");
        final String[] returnArray = new String[0];

        // Mock handler
        final INodeHandler handler = Mockito.mock(INodeHandler.class);
        Mockito.when(handler.isNull(textNode)).thenReturn(false);
        Mockito.when(handler.isEmpty(textNode)).thenReturn(true);
        Mockito.when(handler.newArray()).thenReturn(returnArray);

        // Setup and invoke mapper
        final ArraySplitMapper mapper = new ArraySplitMapper(";", false);
        final Object result = mapper.apply(handler, textNode);

        // Check that we get an empty array
        Assertions.assertEquals(returnArray, result);
    }

    @Test
    @DisplayName("Returns null when given null")
    void handleNull() {

        // Setup data, using raw object as a placeholder for a null node
        final Object nullNode = new Object();

        // Mock handler
        final INodeHandler handler = Mockito.mock(INodeHandler.class);
        Mockito.when(handler.isNull(nullNode)).thenReturn(true);
        Mockito.when(handler.newNull()).thenReturn(null);

        // Setup and invoke mapper
        final ArraySplitMapper mapper = new ArraySplitMapper(";", false);
        final Object result = mapper.apply(handler, nullNode);

        // Check that we get null
        Assertions.assertNull(result);
    }

    @Test
    @DisplayName("Converts 'abc,3fg, 890' into ['abc', '3fg', ' 890']")
    void splitsString() {

        // Setup data, using raw object as a placeholder for a null node
        final FakeStringNode textNode = new FakeStringNode("abc,3fg, 890");

        // Mock handler
        final INodeHandler handler = Mockito.mock(INodeHandler.class);
        Mockito.when(handler.isNull(textNode)).thenReturn(false);
        Mockito.when(handler.isEmpty(textNode)).thenReturn(false);
        Mockito.when(handler.newArray()).thenReturn(new ArrayList());
        Mockito.doAnswer((args) -> {
            ((ArrayList)args.getArgument(0)).add(args.getArgument(1));
            return null;
        }).when(handler).addItem(Mockito.any(), Mockito.any());
        Mockito.when(handler.asText(Mockito.any())).thenAnswer((args) -> ((FakeStringNode)args.getArgument(0)).getText());
        Mockito.when(handler.newText(Mockito.any())).thenAnswer((args) -> new FakeStringNode(args.getArgument(0)));

        // Setup and invoke mapper
        final ArraySplitMapper mapper = new ArraySplitMapper(",", false);
        final Object result = mapper.apply(handler, textNode);

        // Check that we get null
        Assertions.assertEquals(List.of(
                new FakeStringNode("abc"),
                new FakeStringNode("3fg"),
                new FakeStringNode(" 890")
        ), result);
    }

    @Test
    @DisplayName("Converts 'abc,3fg, 890' into ['abc', '3fg', '890']")
    void splitsString_doTrim() {

        // Setup data, using raw object as a placeholder for a null node
        final FakeStringNode textNode = new FakeStringNode("abc,3fg, 890");

        // Mock handler
        final INodeHandler handler = Mockito.mock(INodeHandler.class);
        Mockito.when(handler.isNull(textNode)).thenReturn(false);
        Mockito.when(handler.isEmpty(textNode)).thenReturn(false);
        Mockito.when(handler.newArray()).thenReturn(new ArrayList());
        Mockito.doAnswer((args) -> {
            ((ArrayList)args.getArgument(0)).add(args.getArgument(1));
            return null;
        }).when(handler).addItem(Mockito.any(), Mockito.any());
        Mockito.when(handler.asText(Mockito.any())).thenAnswer((args) -> ((FakeStringNode)args.getArgument(0)).getText());
        Mockito.when(handler.newText(Mockito.any())).thenAnswer((args) -> new FakeStringNode(args.getArgument(0)));

        // Setup and invoke mapper
        final ArraySplitMapper mapper = new ArraySplitMapper(",", true);
        final Object result = mapper.apply(handler, textNode);

        // Check that we get null
        Assertions.assertEquals(List.of(
                new FakeStringNode("abc"),
                new FakeStringNode("3fg"),
                new FakeStringNode("890")
        ), result);
    }
}
