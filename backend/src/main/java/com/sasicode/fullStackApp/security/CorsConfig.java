package com.sasicode.fullStackApp.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {


    @Value("#{'${cors.allowed-origins}'.split(',')}")
    public List<String> allowedOrgins;
    @Value("#{'${cors.allowed-methods}'.split(',')}")
    public List<String> allowedMethods;
    @Value("#{'${cors.allowed-headers}'.split(',')}")
    public List<String> allowedHeaders;
    @Value("#{'${cors.exposed-headers}'.split(',')}")
    public List<String> exposedHeaders;




    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrgins);
        configuration.setAllowedMethods(allowedMethods);
        configuration.setAllowedHeaders(allowedHeaders);
        configuration.setExposedHeaders(exposedHeaders);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("api/v1/**", configuration);
        return source;
    }
}
