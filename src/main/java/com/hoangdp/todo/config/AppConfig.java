package com.hoangdp.todo.config;

import java.time.Instant;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hoangdp.todo.converter.InstantDeserializer;

@Configuration
public class AppConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.deserializerByType(Instant.class, new InstantDeserializer());
    }
}
