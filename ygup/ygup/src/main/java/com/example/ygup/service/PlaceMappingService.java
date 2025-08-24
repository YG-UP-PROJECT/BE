// src/main/java/com/example/ygup/service/PlaceMappingService.java
package com.example.ygup.service;

import com.example.ygup.entity.PlaceMappingEntity;
import com.example.ygup.survey.PlaceMappingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PlaceMappingService {
    private final PlaceMappingRepository repo;

    public PlaceMappingService(PlaceMappingRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void upsert(String kakaoId, String googlePlaceId,
                       String name, String address, Double lat, Double lng) {
        if (kakaoId == null || kakaoId.isBlank()) return;
        var entity = repo.findById(kakaoId)
                .orElse(PlaceMappingEntity.builder().kakaoId(kakaoId).build());
        // 최신으로 갱신
        if (googlePlaceId != null && !googlePlaceId.isBlank()) entity.setGooglePlaceId(googlePlaceId);
        if (name != null) entity.setName(name);
        if (address != null) entity.setAddress(address);
        if (lat != null) entity.setLat(lat);
        if (lng != null) entity.setLng(lng);
        repo.save(entity);
    }

    @Transactional(readOnly = true)
    public Optional<String> findGoogleIdByKakaoId(String kakaoId) {
        return repo.findById(kakaoId).map(PlaceMappingEntity::getGooglePlaceId);
    }
}