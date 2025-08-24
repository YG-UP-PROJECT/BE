// src/main/java/com/example/ygup/dto/AutoRecommendationResponse.java
package com.example.ygup.dto;

import com.example.ygup.dto.RecommendationResponse.Place;
import java.util.List;

public record AutoRecommendationResponse(
        String location,
        List<String> keywords,
        List<PlaceCard> places // ★ 예전 Place 대신 PlaceCard 사용
) {}
