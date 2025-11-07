package com.example.smily_bot.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация OpenAPI/Swagger
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Telegram Bot Dream Stream API")
                        .description("""
                                API для Telegram ботов.
                                
                                ## Эндпоинты
                                - `/api/stickersets/**` - работа со стикерами
                                - `/api/bots/**` - управление ботами
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Dream Stream Team")
                                .email("support@dreamstream.com")
                                .url("https://dreamstream.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
