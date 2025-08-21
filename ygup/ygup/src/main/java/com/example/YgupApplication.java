// src/main/java/com/example/Application.java
package com.example;

import com.example.ygup.publicdata.service.TourApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication  // 기본 스캔: com.example 하위 전체
@EnableConfigurationProperties(TourApiProperties.class)
public class YgupApplication {
	public static void main(String[] args) {
		SpringApplication.run(YgupApplication.class, args);
	}
}
