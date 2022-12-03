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
import org.mockito.Mockito;

import java.util.HashMap;
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
}
