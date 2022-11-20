package com.darkguardsman.json.remap.core.errors;

public class MapperTypeException extends MapperException {

    public MapperTypeException(String message) {
        super(message);
    }

    public MapperTypeException(String message, Exception e) {
        super(message, e);
    }
}
