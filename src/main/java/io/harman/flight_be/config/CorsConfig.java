package io.harman.flight_be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/**")
                        .allowedOrigins(
                                "http://localhost:5173",
                                "http://localhost:3000",
                                "http://localhost:8080",
                                "http://2306210286-fe.hafizmuh.site",
                                "http://2306275506-fe.hafizmuh.site",
                                "http://2306165686-fe.hafizmuh.site",
                                "http://2306240080-fe.hafizmuh.site",
                                "http://2306265843-fe.hafizmuh.site",
                                "http://2306210286-be.hafizmuh.site",
                                "http://2306275506-be.hafizmuh.site",
                                "http://2306165686-be.hafizmuh.site",
                                "http://2306240080-be.hafizmuh.site",
                                "http://2306265843-be.hafizmuh.site"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*", "X-API-KEY")
                        .allowCredentials(true)
                        .exposedHeaders("Authorization", "X-API-KEY");
            }
        };
    }
}