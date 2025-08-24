package com.example.ygup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll()   // ✅ 모든 요청 허용
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable()); // ✅ 기본 로그인도 비활성화
        return http.build();
    }
}
