package com.example.ygup.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 쉼표 구분 문자열을 배열로 안전 분리 (빈값 방지)
    @Value("#{'${app.cors.allowed-origins:*}'.split(',')}")
    private String[] allowedOriginsRaw;

    @Value("#{'${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}'.split(',')}")
    private String[] allowedMethodsRaw;

    @Value("#{'${app.cors.allowed-headers:*}'.split(',')}")
    private String[] allowedHeadersRaw;

    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 공백 제거 + 빈 항목 제거
        String[] allowedOrigins = sanitize(allowedOriginsRaw);
        String[] allowedMethods = sanitize(allowedMethodsRaw);
        String[] allowedHeaders = sanitize(allowedHeadersRaw);

        boolean hasWildcard = Arrays.stream(allowedOrigins)
                .anyMatch(o -> o != null && o.contains("*"));

        // 브라우저 제약: allowCredentials=true && "*" 조합은 허용되지 않음
        boolean effectiveAllowCredentials = allowCredentials && !hasWildcard;

        var reg = registry.addMapping("/**")
                .allowedMethods(allowedMethods)
                .allowedHeaders(allowedHeaders)
                .allowCredentials(effectiveAllowCredentials)
                .maxAge(3600);

        if (hasWildcard) {
            // 와일드카드 사용 시 Spring 6+는 allowedOriginPatterns 권장
            reg.allowedOriginPatterns(allowedOrigins);
        } else {
            reg.allowedOrigins(allowedOrigins);
        }
    }

    private static String[] sanitize(String[] arr) {
        return Arrays.stream(arr == null ? new String[0] : arr)
                .map(s -> s == null ? "" : s.trim())
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }
}