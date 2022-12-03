package com.darkguardsman.json.remap.mappers;

import com.darkguardsman.json.remap.core.imp.INodeHandler;
import com.darkguardsman.json.remap.core.mappers.DoubleMapper;
import com.darkguardsman.json.remap.core.mappers.IntegerMapper;
import com.darkguardsman.json.remap.fakes.FakeNumberNode;
import com.darkguardsman.json.remap.fakes.FakeStringNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class IntegerMapperTest {

    private final IntegerMapper mapper = new IntegerMapper();
    private final INodeHandler nodeHandler = Mockito.mock(INodeHandler.class);

    @Test
    void mapIntegerToFloatNode() {

        // Setup data
        final int intInput = 33;
        final FakeNumberNode doubleNode = new FakeNumberNode(intInput);

        // Mock handler
        Mockito.when(nodeHandler.asInt(doubleNode)).thenReturn(intInput);
        Mockito.when(nodeHandler.newInteger(intInput)).thenReturn(new FakeNumberNode(intInput));

        // Invoke and check we produce a new double node
        Assertions.assertEquals(doubleNode, mapper.apply(nodeHandler, doubleNode));
    }

    @Test
    void mapStringToFloatNode() {

        // Test is half pointless as we are converting in place of the handler. Mostly exists
        //  to enforce that nothing added in the future prevents string parsing.

        // Setup data
        final String stringInput = "33";
        final FakeStringNode stringNode = new FakeStringNode(stringInput);
        final int intInput = 33;
        final FakeNumberNode doubleNode = new FakeNumberNode(intInput);

        // Mock handler
        Mockito.when(nodeHandler.asInt(stringNode)).thenReturn(intInput);
        Mockito.when(nodeHandler.newInteger(intInput)).thenReturn(new FakeNumberNode(intInput));

        // Invoke and check we produce a new double node
        Assertions.assertEquals(doubleNode, mapper.apply(nodeHandler, stringNode));
    }
}
