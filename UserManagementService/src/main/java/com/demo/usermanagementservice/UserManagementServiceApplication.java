package com.demo.usermanagementservice;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableFeignClients
@EnableWebSecurity
@EnableConfigurationProperties
public class UserManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserManagementServiceApplication.class, args);
    }

    @Bean
    public OpenApiCustomizer userManagementApiCustomiser() {
        return openApi -> {
            openApi.info(new Info().title("User Management API"));

            openApi.addSecurityItem(new SecurityRequirement().addList("User Management API Authorisation"));

            openApi.getComponents().addSecuritySchemes(
                    "User Management API Authorisation", new SecurityScheme()
                            .name("User Management API Authorisation")
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("basic"));
        };
    }

}
