package com.hoangdp.todo.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("To Do App API").description("Develop by HoangDP")
                .contact(new Contact().email("info@hoangdp.com").name("Phu").url("https://www.hoangdp.com"))
                .license(new License().name("Apache 2.0")));
    }

    @Bean
    public GroupedOpenApi adminOpenApi() {
        return GroupedOpenApi.builder().group("admin").packagesToScan("com.hoangdp.todo.resource.admin")        
        .build();
    }

        @Bean
    public GroupedOpenApi userOpenApi() {
        return GroupedOpenApi.builder().group("user").packagesToExclude("com.hoangdp.todo.resource.admin")        
        .build();
    }

}
