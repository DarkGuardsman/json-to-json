package com.darkguardsman.json.remap.core.imp;

import com.darkguardsman.json.remap.core.errors.MapperTypeException;

import java.util.stream.Stream;

/**
 * Abstraction around either GSON or Jackson to allow logic to work regardless of system
 */
public interface INodeHandler<T extends Object, O extends T, A extends T> {

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

    boolean asBoolean(T node);

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

    Stream<T> asStream(A array, boolean parallel);


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
     * Adds an item to an array
     * @param array to update
     * @param item to add
     */
    void addItem(A array, T item);

    /**
     * Used to get a field from an object
     *
     * @param object to access
     * @param field to get
     */
    T getField(O object, String field);

    /**
     * Used to get an index of an array
     *
     * @param array to access
     * @param index to get
     * @return item at index
     */
    T getItem(A array, int index);

    /**
     * Checks if an object has a field
     *
     * @param object to access
     * @param field to get
     * @return true if field is contained
     */
    boolean hasField(O object, String field);

    /**
     * Checks if a node is considered empty
     *
     * null -> true
     * [] -> true
     * {} -> true
     * "" -> true
     *
     * @param node to check
     * @return true if empty
     */
    boolean isEmpty(T node);

    /**
     * Checks if a node is null
     *
     * @param input to check
     * @return true if null or null node
     */
    boolean isNull(T input);
}
