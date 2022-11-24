package com.darkguardsman.json.remap.mappers;

import com.darkguardsman.json.remap.core.mappers.AsIsMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AsIsMapperTest {

    private final AsIsMapper mapper = new AsIsMapper();

    @Test
    void returnsInput() {
        // Testing it returns the input, normally this would be a JSON node but for this test any input works
        Assertions.assertEquals("String", mapper.apply(null, "String"));
    }
}
