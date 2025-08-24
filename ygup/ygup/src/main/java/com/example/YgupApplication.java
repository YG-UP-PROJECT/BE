// src/main/java/com/example/YgupApplication.java
package com.example;

import com.example.ygup.publicdata.service.TourApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties(TourApiProperties.class)
// 👇 추가된 부분: com.example.ygup 패키지 하위도 스캔하도록 강제
@ComponentScan(basePackages = "com.example.ygup")
public class YgupApplication {
    public static void main(String[] args) {
        SpringApplication.run(YgupApplication.class, args);
    }
}

