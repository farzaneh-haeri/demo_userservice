package com.demo.usermanagementservice.security;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class CustomSpringSecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable).
                authorizeHttpRequests(auth ->
                        auth.requestMatchers("/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui/**").permitAll()
                                .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

}
