package com.lms.operation;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "LMS Operation Service",
                version = "1.0",
                description = "Loan Management System — Operation API"
        )
)
public class OperationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OperationServiceApplication.class, args);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("https://loan.cavkim.tech").description("Production"))
                .addServersItem(new Server().url("http://localhost:8080").description("Local Development"));
    }
}