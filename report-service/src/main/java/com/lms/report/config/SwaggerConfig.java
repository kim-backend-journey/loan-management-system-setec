package com.lms.report.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI reportServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LMS Report Service")
                        .version("1.0")
                        .description("Loan Management System — Report API"))
                .addServersItem(new Server().url("https://loan.cavkim.tech").description("Production"))
                .addServersItem(new Server().url("http://localhost:8080").description("Local Development"));
    }
}