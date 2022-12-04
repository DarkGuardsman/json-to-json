package com.darkguardsman.json.remap.nodes;

import com.darkguardsman.json.remap.core.imp.INodeHandler;
import com.darkguardsman.json.remap.core.nodes.MapperNode;
import com.darkguardsman.json.remap.core.nodes.MapperNodeArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapperNodeArrayTest {

    @Test
    void mapArrayEmpty() {
        // Setup data
        Object[] inputData = new Object[] {};

        // Mock handler
        final INodeHandler handler = Mockito.mock(INodeHandler.class);
        Mockito.when(handler.isEmpty(Mockito.any())).thenReturn(true);
        Mockito.when(handler.newArray()).thenReturn(new ArrayList());

        // Trigger mapper
        final MapperNodeArray arrayNode = new MapperNodeArray();
        final Object result = arrayNode.apply(handler, inputData, "123");

        // Check that we get an empty array
        Assertions.assertEquals(List.of(), result);
    }

    @Test
    void mapArray() {

        // Setup data
        Object[] inputData = new Object[] {"a", "b", "c"};

        // Mock handler
        final INodeHandler handler = Mockito.mock(INodeHandler.class);

        Mockito.when(handler.isEmpty(inputData)).thenReturn(false);
        Mockito.when(handler.asArray(inputData)).thenReturn(inputData);
        Mockito.when(handler.newArray()).thenReturn(new ArrayList());
        Mockito.when(handler.asStream(inputData, false)).thenReturn(Arrays.stream(inputData));
        Mockito.doAnswer((args) -> {
            ((ArrayList)args.getArgument(0)).add(args.getArgument(1));
            return null;
        }).when(handler).addItem(Mockito.any(), Mockito.any());

        // Mock item mapper
        final MapperNode itemMapper = Mockito.mock(MapperNode.class);
        Mockito.when(itemMapper.apply(handler, "a", "123")).thenReturn(1);
        Mockito.when(itemMapper.apply(handler, "b", "123")).thenReturn("2");
        Mockito.when(itemMapper.apply(handler, "c", "123")).thenReturn("tree");

        // Setup mapper using mocked item mapper
        final MapperNodeArray arrayNode = new MapperNodeArray();
        arrayNode.setItemMapper(itemMapper);

        // Trigger mapper
        final Object result = arrayNode.apply(handler, inputData, "123");

        // Validate we get a new array containing mocked returns from item mapper
        Assertions.assertEquals(List.of(1, "2", "tree"), result);
    }
}
