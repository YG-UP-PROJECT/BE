// src/main/java/com/example/ygup/YgupApplication.java
package com.example.ygup;

import com.example.ygup.publicdata.service.TourApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(TourApiProperties.class)
public class YgupApplication {

	public static void main(String[] args) {
		SpringApplication.run(YgupApplication.class, args);
	}
}
