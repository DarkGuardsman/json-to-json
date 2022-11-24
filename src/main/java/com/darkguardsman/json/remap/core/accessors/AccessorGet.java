package com.darkguardsman.json.remap.core.accessors;

import com.darkguardsman.json.remap.core.errors.MapperException;
import com.darkguardsman.json.remap.core.imp.IAccessorFunc;
import com.darkguardsman.json.remap.core.imp.INodeHandler;

/**
 * Simple lookup using field names. Doesn't support more complex lookups using
 * key characters. As it is possible for most special characters to end up as part
 * of a badly designed field name. Only exception to this is '.' in order for the system
 * to be able to split while not requiring the user to define an array.
 *
 * Examples:
 * *
 */
public class AccessorGet<T, O extends T, A extends T> implements IAccessorFunc<T> {

    private final INodeHandler<T, O, A> handler;
    private final boolean useRoot;
    private final String[] fields;
    private final String accessor;

    /**
     * @param handler used to check if fields are objects, arrays, etc
     * @param accessor string to use for lookups. Splits on '.', if something used '.' in a field name then they need to use object based lookups
     * @param useRoot will be true when lookup is from the root json element
     */
    public AccessorGet(INodeHandler<T, O, A> handler, String accessor, boolean useRoot) {
        this.accessor = accessor;
        this.useRoot = useRoot;
        this.handler = handler;
        this.fields = accessor.split("[.]");
    }

    @Override
    public T apply(T node, T root) {
        T value = useRoot ? root : node;

        // Dive fields in attempt to resolve full path
        for (String field : fields) {

            // Validate we are an object, if not throw an error as it likely means our input is invalid or mapper is outdated
            if (handler.isObject(value)) {
                value = handler.getField(handler.asObject(value), field);
                if (value == null) {
                    return null;
                }
            }
            // Get by index
            else if(handler.isArray(value)) {

                try {
                    int index = Integer.parseInt(field);
                    value = handler.getItem(handler.asArray(value), index);
                }
                catch(NumberFormatException e) {
                    final String formattedMessage = String.format("Failed to access '%s' as field '%s' was not an index for the array found, node=%s", accessor, field, value);
                    throw new MapperException(formattedMessage);
                }
            }
            else {
                final String formattedMessage = String.format("Failed to access '%s' as field '%s' was not an object, node=%s", accessor, field, value);
                throw new MapperException(formattedMessage);
            }
        }
        return value;
    }
}
