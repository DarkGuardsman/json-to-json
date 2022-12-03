package com.darkguardsman.json.remap.core.mappers;

import com.darkguardsman.json.remap.core.MapperLoader;
import com.darkguardsman.json.remap.core.imp.IMapperFunc;
import com.darkguardsman.json.remap.core.imp.INodeHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ArrayJoinMapper<T, O extends T, A extends T> implements IMapperFunc<T, O, A> {

    public static final String TYPE_ARRAY_JOIN = "array.join";

    private static final String FIELD_JOINER = "joiner";
    private static final String JOINER_DEFAULT = ",";

    public static <T, O extends T, A extends T> void register(MapperLoader<T, O, A> loader) {
        loader.addMapperBuilder(TYPE_ARRAY_JOIN, ArrayJoinMapper::build);
    }

    public static <T, O extends T, A extends T> ArrayJoinMapper<T, O, A> build(MapperLoader<T, O, A> loader, O source) {
        final INodeHandler<T, O, A> factory = loader.getHandler();
        final T joinerField = factory.getField(source, FIELD_JOINER);
        final String splitter = Optional.ofNullable(joinerField).map(factory::asText).orElse(JOINER_DEFAULT);
        return new ArrayJoinMapper(splitter);
    }

    @Getter
    private final String joiner;

    @Override
    public T apply(INodeHandler<T, O, A> factory, T input) {
        //Handle null
        if(factory.isNull(input)) {
            return factory.newNull();
        }

        // Cast to array
        final A arrayNode = factory.asArray(input);

        //Handle empty array as null TODO allow override with empty string or other default output for edge case legacy
        if(factory.isEmpty(arrayNode)) {
            return factory.newNull();
        }

        //Join array as string and return
        final String joinedText = factory.asStream(arrayNode, false)
                .map(factory::asText)
                .collect(Collectors.joining(joiner));
        return factory.newText(joinedText);
    }
}
