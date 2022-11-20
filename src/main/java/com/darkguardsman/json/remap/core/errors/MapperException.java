package com.darkguardsman.json.remap.core.errors;

public class MapperException extends RuntimeException {

    public MapperException(String message) {
        super(message);
    }

    public MapperException(String message, Exception e) {
        super(message, e);
    }
}
