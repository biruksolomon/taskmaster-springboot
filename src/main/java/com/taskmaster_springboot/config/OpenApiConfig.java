package com.taskmaster_springboot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfig {

    List<Server> servers = new ArrayList<>();


    @Bean
    public OpenAPI customOpenAPI() {
        servers.add(new Server()
                .url("http://localhost:9090" )
                .description("Local Development Server"));
        return new OpenAPI()
                .info(new Info()
                        .title("TaskMaster" + " API" + " Developed By Biruk Solomon")
                        .version("V1")
                        .description( "TaskMaster is a backend API built with Java Spring Boot 3, designed to manage projects, tasks, teams, and roles efficiently.\n" +
                                "It provides secure authentication, email communication, and real-time collaboration — all built with clean RESTful architecture.\n" +
                                "Ideal for freelance, enterprise, or internal productivity solutions.")

                        .contact(new Contact()
                                .name("TaskMaster" + " Developer")
                                .email("biruksolomonmoges@gmail.com")
                                )
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(servers)
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createBearerAuthScheme())
                        .addSecuritySchemes("API Key", createApiKeyScheme()));
    }

    private SecurityScheme createBearerAuthScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer")
                .name("Authorization")
                .description("Enter JWT Bearer token in the format: Bearer {token}");
    }

    private SecurityScheme createApiKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("X-API-Key")
                .description("API Key for service-to-service authentication");
    }
}
