package com.darkguardsman.json.remap.mappers;

import com.darkguardsman.json.remap.core.imp.INodeHandler;
import com.darkguardsman.json.remap.core.mappers.DoubleMapper;
import com.darkguardsman.json.remap.fakes.FakeNumberNode;
import com.darkguardsman.json.remap.fakes.FakeStringNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class DoubleMapperTest {

    private final DoubleMapper mapper = new DoubleMapper();
    private final INodeHandler nodeHandler = Mockito.mock(INodeHandler.class);

    @Test
    void mapDoubleToFloatNode() {

        // Setup data
        final double doubleInput = 33.4;
        final FakeNumberNode doubleNode = new FakeNumberNode(doubleInput);

        // Mock handler
        Mockito.when(nodeHandler.asDouble(doubleNode)).thenReturn(doubleInput);
        Mockito.when(nodeHandler.newFloating(doubleInput)).thenReturn(new FakeNumberNode(doubleInput));

        // Invoke and check we produce a new double node
        Assertions.assertEquals(doubleNode, mapper.apply(nodeHandler, doubleNode));
    }

    @Test
    void mapStringToFloatNode() {

        // Test is half pointless as we are converting in place of the handler. Mostly exists
        //  to enforce that nothing added in the future prevents string parsing.

        // Setup data
        final String stringInput = "33.4";
        final FakeStringNode stringNode = new FakeStringNode(stringInput);
        final double doubleInput = 33.4;
        final FakeNumberNode doubleNode = new FakeNumberNode(doubleInput);

        // Mock handler
        Mockito.when(nodeHandler.asDouble(stringNode)).thenReturn(doubleInput);
        Mockito.when(nodeHandler.newFloating(doubleInput)).thenReturn(new FakeNumberNode(doubleInput));

        // Invoke and check we produce a new double node
        Assertions.assertEquals(doubleNode, mapper.apply(nodeHandler, stringNode));
    }
}
