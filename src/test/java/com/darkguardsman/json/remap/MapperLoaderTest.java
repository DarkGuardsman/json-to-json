package com.darkguardsman.json.remap;

import com.darkguardsman.json.remap.core.MapperLoader;
import com.darkguardsman.json.remap.core.accessors.AccessorGet;
import com.darkguardsman.json.remap.core.imp.IAccessorFunc;
import com.darkguardsman.json.remap.core.imp.INodeHandler;
import com.darkguardsman.json.remap.fakes.FakeBooleanNode;
import com.darkguardsman.json.remap.fakes.FakeNodeHandler;
import com.darkguardsman.json.remap.fakes.FakeStringNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class MapperLoaderTest {

    final INodeHandler handler = new FakeNodeHandler();

    @Test
    void testAccessorString() {

        // Setup data
        final FakeStringNode textNode = new FakeStringNode("field1");

        // Invoke loading of accessor function
        final MapperLoader loader = new MapperLoader(handler);
        final IAccessorFunc func = loader.loadAccessor(textNode);

        // Verify we created a new simple getter
        Assertions.assertSame(AccessorGet.class, func.getClass());
        Assertions.assertEquals("field1", ((AccessorGet)func).getAccessor());
        Assertions.assertEquals(false, ((AccessorGet)func).isUseRoot());
        Assertions.assertEquals(handler, ((AccessorGet)func).getHandler());
    }

    //TODO test for blank string

    @Test
    void testAccessorObject() {

        // Setup data, using object as placeholder
        final HashMap<String, Object> nodeObject = new HashMap();
        nodeObject.put("root", new FakeBooleanNode(true));
        nodeObject.put("field",  new FakeStringNode("field2"));
        nodeObject.put("type",  new FakeStringNode("get"));

        // Invoke loading of accessor function
        final MapperLoader loader = new MapperLoader(handler);
        final IAccessorFunc func = loader.loadAccessor(nodeObject);

        // Verify we created a new simple getter
        Assertions.assertSame(AccessorGet.class, func.getClass());
        Assertions.assertEquals("field2", ((AccessorGet)func).getAccessor());
        Assertions.assertEquals(true, ((AccessorGet)func).isUseRoot());
        Assertions.assertEquals(handler, ((AccessorGet)func).getHandler());
    }

    // TODO test for blank string on object 'field'
    // TODO test for null string on object 'field'
    // TODO test for null string on object 'type'
    // TODO test for blank string on object 'type'

    // TODO test array accessor

    // TODO test mapper simple
    // TODO test mapper builder
    // TODO test missing mapper type
    // TODO test null mapper type
    // TODO test blank mapper type

    // TODO test loading node object
    // TODO test loading node array
    // TODO test loading node field

    // TODO test loading mapper data

    // TODO test registering mapper simple
    // TODO test registering mapper builder
    // TODO test registering defaults
}
