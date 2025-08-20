// src/main/java/com/example/ygup/publicdata/repository/AttractionImageRepository.java
package com.example.ygup.publicdata.repository;

import com.example.ygup.publicdata.entity.AttractionImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttractionImageRepository extends JpaRepository<AttractionImage, Long> {
    List<AttractionImage> findByContentId(Long contentId);
    void deleteByContentId(Long contentId);
}
