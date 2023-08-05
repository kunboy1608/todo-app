package com.hoangdp.todo.converter;

import java.io.IOException;
import java.time.Instant;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import com.hoangdp.todo.utils.TimeUtils;

public class InstantDeserializer extends StdDeserializer<Instant> {

    protected InstantDeserializer(Class<?> vc) {
        super(vc);
    }

    public InstantDeserializer() {
        super(Instant.class);
    }

    @Override
    public Instant deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JacksonException {        
        return TimeUtils.instantFromString(jp.getValueAsString());
    }
}
