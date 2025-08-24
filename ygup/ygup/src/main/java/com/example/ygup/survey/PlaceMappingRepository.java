// src/main/java/com/example/ygup/survey/PlaceMappingRepository.java
package com.example.ygup.survey;

import com.example.ygup.entity.PlaceMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceMappingRepository extends JpaRepository<PlaceMappingEntity, String> {}
