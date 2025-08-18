package com.example.ygup.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins:*}")
    private String[] allowedOrigins; // 쉼표로 구분된 문자열을 배열로 바인딩

    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String[] allowedMethods;

    @Value("${app.cors.allowed-headers:*}")
    private String[] allowedHeaders;

    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 와일드카드(*)가 포함되어 있으면 allowedOriginPatterns 사용 (Spring 6 정책)
        boolean hasWildcard = Arrays.stream(allowedOrigins)
                .anyMatch(o -> o != null && o.contains("*"));

        var reg = registry.addMapping("/**")
                .allowedMethods(allowedMethods)
                .allowedHeaders(allowedHeaders)
                .allowCredentials(allowCredentials)
                .maxAge(3600);

        if (hasWildcard) {
            reg.allowedOriginPatterns(allowedOrigins);
        } else {
            reg.allowedOrigins(allowedOrigins);
        }
    }
}
