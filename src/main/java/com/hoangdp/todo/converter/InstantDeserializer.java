package com.hoangdp.todo.converter;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class InstantDeserializer extends StdDeserializer<Instant>{

    private static final String PATTERN_FORMAT = "yyyy-MM-dd HH:mm:ss";

    protected InstantDeserializer(Class<?> vc) {
        super(vc);        
    }

    public InstantDeserializer(){
        super(Instant.class);
    }

    @Override
    public Instant deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JacksonException {                        

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT);
        LocalDateTime localDateTime = LocalDateTime.parse(jp.getValueAsString(), formatter); 
        return localDateTime.toInstant(ZoneOffset.of("+07:00"));
    }    
}
