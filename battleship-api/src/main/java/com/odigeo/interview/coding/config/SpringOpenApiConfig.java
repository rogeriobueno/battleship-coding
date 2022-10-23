package com.odigeo.interview.coding.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringOpenApiConfig {

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Battleship Game")
                        .version("V1")
                        .description("Battleship Game")
                        .termsOfService("http://www.battleshipgame.com"));
    }
}
