package com.darkguardsman.json.remap.core;

import com.darkguardsman.json.remap.core.errors.MapperException;
import com.darkguardsman.json.remap.core.errors.MapperTypeException;

/**
 * Abstraction around either GSON or Jackson to allow logic to work regardless of system
 */
public interface NodeHandler<T extends Object, O extends T, A extends T> {

    /**
     * Creates a new object node
     *
     * @return node
     */
    O newObject();

    /**
     * Creates a new array node
     *
     * @return node
     */
    A newArray();

    /**
     * Creates a new null node
     *
     * @return node
     */
    T newNull();

    /**
     * Creates a new integer node
     *
     * @return node
     */
    T newInteger(int num);

    /**
     * Creates a new floating point node
     *
     * @return node
     */
    T newFloating(double num);

    /**
     * Creates a new text/string node
     *
     * @return node
     */
    T newText(String text);

    String asText(T node);

    int asInt(T node);

    double asDouble(T node);

    /**
     * Used to cast the node to an object type
     *
     * @param node to cast
     * @return node as an object
     * @throws MapperTypeException when node is not an object
     */
    O asObject(T node) throws MapperTypeException;


    /**
     * Used to cast the node to an array type
     *
     * @param node to cast
     * @return node as an array
     * @throws MapperTypeException when node is not an object
     */
    A asArray(T node) throws MapperTypeException;


    boolean isObject(T node);

    boolean isArray(T node);

    /**
     * Used to set a field on an object
     *
     * @param object to set the field onto
     * @param field to set as
     * @param node to store on that field
     */
    void setField(O object, String field, T node);

    /**
     * Used to get a field from an object
     *
     * @param object to access
     * @param field to get
     */
    T getField(O object, String field);

    /**
     * Checks if an object has a field
     *
     * @param object to access
     * @param field to get
     * @return true if field is contained
     */
    boolean hasField(O object, String field);
}
