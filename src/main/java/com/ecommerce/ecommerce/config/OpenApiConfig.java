package com.ecommerce.ecommerce.config;

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
 * OpenAPI/Swagger Configuration
 * JWT Bearer Token Authentication için yapılandırma
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Security Scheme tanımı (JWT Bearer Token)
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // API Bilgileri
                .info(new Info()
                        .title("E-Commerce API")
                        .version("1.0.0")
                        .description("E-Commerce Backend REST API Documentation")
                        .contact(new Contact()
                                .name("E-Commerce Team")
                                .email("contact@ecommerce.com")
                                .url("https://ecommerce.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))

                // Security Scheme (JWT Bearer Token)
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))

                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Bearer Token - Login endpoint'inden alınan access token'ı buraya yapıştırın")));
    }
}
