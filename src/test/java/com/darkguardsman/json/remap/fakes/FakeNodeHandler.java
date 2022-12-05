package com.darkguardsman.json.remap.fakes;

import com.darkguardsman.json.remap.core.errors.MapperTypeException;
import com.darkguardsman.json.remap.core.imp.INodeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * Version of node handler for use with testing to avoid a long list of mocks
 */
public class FakeNodeHandler implements INodeHandler<Object, HashMap, ArrayList> {

    private final Object nullObject = new Object();

    @Override
    public HashMap newObject() {
        return new HashMap();
    }

    @Override
    public ArrayList newArray() {
        return new ArrayList();
    }

    @Override
    public Object newNull() {
        return nullObject;
    }

    @Override
    public FakeNumberNode<Integer> newInteger(int num) {
        return new FakeNumberNode<Integer>(num);
    }

    @Override
    public FakeNumberNode<Double> newFloating(double num) {
        return new FakeNumberNode<>(num);
    }

    @Override
    public FakeStringNode newText(String text) {
        return new FakeStringNode(text);
    }

    @Override
    public String asText(Object node) {
        if(node instanceof FakeStringNode textNode) {
            return textNode.getText();
        }
        throw new IllegalArgumentException("Not a text node");
    }

    @Override
    public boolean asBoolean(Object node) {
        if(node instanceof FakeBooleanNode booleanNode) {
            return booleanNode.getValue();
        }
        throw new IllegalArgumentException("Not a boolean node");
    }

    @Override
    public int asInt(Object node) {
        if(node instanceof FakeNumberNode numberNode) {
            return numberNode.getNumber().intValue();
        }
        throw new IllegalArgumentException("Not a number node");
    }

    @Override
    public double asDouble(Object node) {
        if(node instanceof FakeNumberNode numberNode) {
            return numberNode.getNumber().doubleValue();
        }
        throw new IllegalArgumentException("Not a number node");
    }

    @Override
    public HashMap asObject(Object node) throws MapperTypeException {
        if(node instanceof HashMap map) {
            return map;
        }
        throw new MapperTypeException("Not an object node");
    }

    @Override
    public ArrayList asArray(Object node) throws MapperTypeException {
        if(node instanceof ArrayList arrayList) {
            return arrayList;
        }
        throw new MapperTypeException("Not an array node");
    }

    @Override
    public Stream<Object> asStream(ArrayList array, boolean parallel) {
        return parallel ? array.parallelStream() : array.stream();
    }

    @Override
    public boolean isObject(Object node) {
        return node instanceof HashMap;
    }

    @Override
    public boolean isArray(Object node) {
        return node instanceof ArrayList;
    }

    @Override
    public void setField(HashMap object, String field, Object node) {
        object.put(field, node);
    }

    @Override
    public void addItem(ArrayList array, Object item) {
        array.add(item);
    }

    @Override
    public Object getField(HashMap object, String field) {
        return object.get(field);
    }

    @Override
    public Object getItem(ArrayList array, int index) {
        return array.get(index);
    }

    @Override
    public boolean hasField(HashMap object, String field) {
        return object.containsKey(field);
    }

    @Override
    public boolean isEmpty(Object node) {
        if(node instanceof HashMap map) {
            return map.isEmpty();
        }
        else if(node instanceof ArrayList arrayList) {
            return arrayList.isEmpty();
        }
        else if(node instanceof FakeStringNode textNode) {
            return textNode.getText() == null || textNode.getText().isBlank();
        }
        return node == nullObject;
    }

    @Override
    public boolean isNull(Object input) {
        return input == nullObject;
    }
}
