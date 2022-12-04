package com.darkguardsman.json.remap.nodes;

import com.darkguardsman.json.remap.core.imp.IAccessorFunc;
import com.darkguardsman.json.remap.core.imp.IMapperFunc;
import com.darkguardsman.json.remap.core.imp.INodeHandler;
import com.darkguardsman.json.remap.core.nodes.MapperNode;
import com.darkguardsman.json.remap.fakes.FakeNumberNode;
import com.darkguardsman.json.remap.fakes.FakeStringNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MapperNodeTest {

    @Test
    void noMapperNorAccessor() {
        final MapperNode mapperNode = new MapperNode();

        // Using strings as placeholders, we should get the first string and node use handle
        final Object result = mapperNode.apply(null, "2", "1");
        Assertions.assertEquals("2", result);
    }

    @Test
    void noMapperHasAccessor() {
        final MapperNode mapperNode = new MapperNode();
        final IAccessorFunc accessorFunc = Mockito.spy(new IAccessorFunc() {
            // lambdas are considered final :(
            @Override
            public Object apply(Object node, Object root) {
                return "abc";
            }
        });
        mapperNode.setAccessor(accessorFunc);

        // Using strings as placeholders, both should be ignored in favor of the accessor return
        final Object result = mapperNode.apply(null, "a", "b");

        // Verify we got the accessor's return
        Assertions.assertEquals("abc", result);

        // Verify we passed node and root to the accessor function
        Mockito.verify(accessorFunc).apply("a", "b");
    }

    @Test
    void mapperNoAccessor() {
        final INodeHandler handler = Mockito.mock(INodeHandler.class);
        final MapperNode mapperNode = new MapperNode();
        final IMapperFunc mapperFunc = Mockito.spy(new IMapperFunc() {
            // lambdas are considered final :(
            @Override
            public Object apply(INodeHandler handler, Object input) {
                return 3;
            }
        });
        mapperNode.setMapper(mapperFunc);

        // Using strings as placeholders, both should be ignored in favor of the accessor return
        final Object result = mapperNode.apply(handler, "a", "b");

        // Verify we got the accessor's return
        Assertions.assertEquals(3, result);

        // Verify we passed handle and node to mapper
        Mockito.verify(mapperFunc).apply(handler, "a");
    }

    @Test
    void mapperWithAccessor() {
        final INodeHandler handler = Mockito.mock(INodeHandler.class);
        final MapperNode mapperNode = new MapperNode();

        final IMapperFunc mapperFunc = Mockito.spy(new IMapperFunc() {
            // lambdas are considered final :(
            @Override
            public Object apply(INodeHandler handler, Object input) {
                return 3;
            }
        });
        mapperNode.setMapper(mapperFunc);

        final IAccessorFunc accessorFunc = Mockito.spy(new IAccessorFunc() {
            // lambdas are considered final :(
            @Override
            public Object apply(Object node, Object root) {
                return "abc";
            }
        });
        mapperNode.setAccessor(accessorFunc);

        // Using strings as placeholders, both should be ignored in favor of the accessor return
        final Object result = mapperNode.apply(handler, "a", "b");

        // Verify we got the accessor's return
        Assertions.assertEquals(3, result);

        // Verify we passed node and root to the accessor function
        Mockito.verify(accessorFunc).apply("a", "b");

        // Verify we passed handle and new node to mapper
        Mockito.verify(mapperFunc).apply(handler, "abc");
    }
}
