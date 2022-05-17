package com.plourenco.credix;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.time.LocalDate;

public enum QuestionType {

    STRING(value -> true),
    INTEGER(Integer::parseInt),
    BOOLEAN(Boolean::valueOf),
    DATE(LocalDate::parse),
    TUPLE(value -> {
        try {
            return new ObjectMapper().readValue(value, Pair.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    });

    private final TypeValidator validator;

    QuestionType(TypeValidator validator) {
        this.validator = validator;
    }

    public Object validate(String value) throws IllegalArgumentException {
        return validator.validate(value);
    }
}

interface TypeValidator {
    Object validate(String value) throws IllegalArgumentException;
}

@JsonDeserialize(using = PairDeserializer.class)
class Pair<T, K> {
    T key;
    K value;

    public Pair(T key, K value) {
        this.key = key;
        this.value = value;
    }

    public T getKey() {
        return key;
    }

    public void setKey(T key) {
        this.key = key;
    }

    public K getValue() {
        return value;
    }

    public void setValue(K value) {
        this.value = value;
    }
}

class PairDeserializer extends StdDeserializer<Pair<?, ?>> {

    public PairDeserializer() {
        this(null);
    }

    public PairDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Pair<?, ?> deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        if (node.isArray()) {
            ArrayNode array = (ArrayNode) node;
            if (node.size() == 2) {
                return new Pair<>(array.get(0), array.get(1));
            }
        }
        throw new IllegalArgumentException();
    }
}
