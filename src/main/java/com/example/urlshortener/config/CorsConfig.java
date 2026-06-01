package com.example.urlshortener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {

                // Allow localhost for development and an optional FRONTEND_URL env var for production
                String frontend = System.getenv("FRONTEND_URL");

                if (frontend != null && !frontend.isBlank()) {
                    registry.addMapping("/**")
                            .allowedOrigins("http://localhost:5173", frontend)
                            .allowedMethods("*");
                } else {
                    registry.addMapping("/**")
                            .allowedOrigins("http://localhost:5173")
                            .allowedMethods("*");
                }

            }

        };

    }

}