package com.miniscore.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI coreRegistryOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Mini-Score Core Registry API")
                .version("1.0")
                .description("Simple reference service for leagues, teams, players, and roster lookup."));
    }
}
