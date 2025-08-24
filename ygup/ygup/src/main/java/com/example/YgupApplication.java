// src/main/java/com/example/YgupApplication.java
package com.example;

import com.example.ygup.publicdata.service.TourApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties(TourApiProperties.class)
// 👇 두 패키지를 다 스캔하도록 수정
@ComponentScan(basePackages = {
        "com.example.ygup",
        "com.example.ygupgoogle"
})
public class YgupApplication {
    public static void main(String[] args) {
        SpringApplication.run(YgupApplication.class, args);
    }
}
