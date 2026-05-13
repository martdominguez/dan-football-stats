package com.miniscore.live.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI liveEngineOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Mini-Score Live Engine API")
                .version("1.0")
                .description("Simple reference service for live match actions and event publishing."));
    }
}
