package com.darkguardsman.json.remap.mappers;

import com.darkguardsman.json.remap.core.imp.INodeHandler;
import com.darkguardsman.json.remap.core.mappers.DoubleMapper;
import com.darkguardsman.json.remap.core.mappers.StringMapper;
import com.darkguardsman.json.remap.fakes.FakeNumberNode;
import com.darkguardsman.json.remap.fakes.FakeStringNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class StringMapperTest {

    private final StringMapper mapper = new StringMapper();
    private final INodeHandler nodeHandler = Mockito.mock(INodeHandler.class);

    @Test
    void mapString() {

        // Setup data
        final FakeStringNode textNode = new FakeStringNode("abc");

        // Mock handler
        Mockito.when(nodeHandler.asText(Mockito.any())).thenAnswer((args) -> ((FakeStringNode)args.getArgument(0)).getText());
        Mockito.when(nodeHandler.newText(Mockito.any())).thenAnswer((args) -> new FakeStringNode(args.getArgument(0)));

        final FakeStringNode result = (FakeStringNode) mapper.apply(nodeHandler, textNode);

        // Invoke and check we produce a new double node
        Assertions.assertEquals("abc", result.getText());
    }
}
