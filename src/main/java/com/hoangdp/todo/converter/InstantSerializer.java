package com.hoangdp.todo.converter;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class InstantSerializer extends JsonSerializer<Instant>{

    private static final String PATTERN_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {        
        final String str = DateTimeFormatter.ofPattern(PATTERN_FORMAT).withZone(ZoneOffset.of("+07:00")).format(value);
        gen.writeString(str);
    }
    
}
