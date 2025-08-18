// src/main/java/com/example/ygup/publicdata/repository/AttractionRepository.java
package com.example.ygup.publicdata.repository;

import com.example.ygup.publicdata.entity.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttractionRepository extends JpaRepository<Attraction, Long> { }
