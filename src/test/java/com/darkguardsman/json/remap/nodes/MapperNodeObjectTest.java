package com.darkguardsman.json.remap.nodes;

import com.darkguardsman.json.remap.core.errors.MapperException;
import com.darkguardsman.json.remap.core.imp.INodeHandler;
import com.darkguardsman.json.remap.core.nodes.MapperNode;
import com.darkguardsman.json.remap.core.nodes.MapperNodeObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

public class MapperNodeObjectTest {

    @Test
    void throwsErrorWhenFieldNameMissing() {
        // Setup data, using base object as placeholder
        final Object nodeObject = new Object();
        final Object rootObject = new Object();

        final Object newObject = new Object();

        // Mock handler
        final INodeHandler handler = Mockito.mock(INodeHandler.class);
        Mockito.when(handler.newObject()).thenReturn(newObject);
        Mockito.doNothing().when(handler).setField(Mockito.any(), Mockito.any(), Mockito.any());

        final MapperNode mapperNodeA = Mockito.spy(new MapperNode<>());
        mapperNodeA.setFieldName("field1");
        mapperNodeA.setAccessor((a, b) -> "abc");

        final MapperNode mapperNodeB = Mockito.spy(new MapperNode<>());
        mapperNodeB.setAccessor((a, b) -> 123);

        // Setup mapper
        final MapperNodeObject mapperObject = new MapperNodeObject();
        mapperObject.setFields(List.of(
                mapperNodeA,
                mapperNodeB
        ));

        MapperException exception = Assertions.assertThrows(MapperException.class, () -> mapperObject.apply(handler, nodeObject, rootObject));
        Assertions.assertTrue(exception.getMessage().startsWith("Missing field name while mapping object; mapper="));
    }

    @Test
    void throwsErrorWhenFieldNameBlank() {
        // Setup data, using base object as placeholder
        final Object nodeObject = new Object();
        final Object rootObject = new Object();

        final Object newObject = new Object();

        // Mock handler
        final INodeHandler handler = Mockito.mock(INodeHandler.class);
        Mockito.when(handler.newObject()).thenReturn(newObject);
        Mockito.doNothing().when(handler).setField(Mockito.any(), Mockito.any(), Mockito.any());

        final MapperNode mapperNodeA = Mockito.spy(new MapperNode<>());
        mapperNodeA.setFieldName("field1");
        mapperNodeA.setAccessor((a, b) -> "abc");

        final MapperNode mapperNodeB = Mockito.spy(new MapperNode<>());
        mapperNodeB.setFieldName(" ");
        mapperNodeB.setAccessor((a, b) -> 123);

        // Setup mapper
        final MapperNodeObject mapperObject = new MapperNodeObject();
        mapperObject.setFields(List.of(
                mapperNodeA,
                mapperNodeB
        ));

        MapperException exception = Assertions.assertThrows(MapperException.class, () -> mapperObject.apply(handler, nodeObject, rootObject));
        Assertions.assertTrue(exception.getMessage().startsWith("Missing field name while mapping object; mapper="));
    }

    @Test
    void mapObject() {
        // Setup data, using base object as placeholder
        final Object nodeObject = new Object();
        final Object rootObject = new Object();

        final Object newObject = new Object();

        // Mock handler
        final INodeHandler handler = Mockito.mock(INodeHandler.class);
        Mockito.when(handler.newObject()).thenReturn(newObject);
        Mockito.doNothing().when(handler).setField(Mockito.any(), Mockito.any(), Mockito.any());

        final MapperNode mapperNodeA = Mockito.spy(new MapperNode<>());
        mapperNodeA.setFieldName("field1");
        mapperNodeA.setAccessor((a, b) -> "abc");

        final MapperNode mapperNodeB = Mockito.spy(new MapperNode<>());
        mapperNodeB.setFieldName("field2");
        mapperNodeB.setAccessor((a, b) -> 123);

        // Setup mapper
        final MapperNodeObject mapperObject = new MapperNodeObject();
        mapperObject.setFields(List.of(
                mapperNodeA,
                mapperNodeB
        ));

        // Trigger mapper
        final Object result = mapperObject.apply(handler, nodeObject, rootObject);

        // Check that we get back the new object created
        Assertions.assertEquals(newObject, result);

        // Check that we invoked each field mapper
        Mockito.verify(mapperNodeA).apply(handler, nodeObject, rootObject);
        Mockito.verify(mapperNodeB).apply(handler, nodeObject, rootObject);

        // Check that we invoked the set field calls
        Mockito.verify(handler).setField(newObject, "field1", "abc");
        Mockito.verify(handler).setField(newObject, "field2", 123);
    }
}
