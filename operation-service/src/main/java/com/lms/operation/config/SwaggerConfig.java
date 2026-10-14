package com.lms.operation.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LMS Operation Service")
                        .version("1.0")
                        .description("Loan Management System — Operation API"))
                .addServersItem(new Server().url("https://loan.cavkim.tech").description("Production"))
                .addServersItem(new Server().url("http://localhost:8080").description("Gateway"))
                .addServersItem(new Server().url("http://localhost:8082").description("Direct (operation-service)"))
                .components(new Components().addSecuritySchemes(SCHEME_NAME,
                        new SecurityScheme()
                                .name(SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                // require the bearer token on every operation so "Authorize" actually attaches it
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME));
    }
}
