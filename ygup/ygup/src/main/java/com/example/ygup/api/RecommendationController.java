package com.example.ygup.api;

import com.example.ygup.dto.RecommendationResponse;
import com.example.ygup.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/sample")
    public RecommendationResponse getSampleMapPoints() {
        return new RecommendationResponse(
                "부천시 역곡동",
                java.util.List.of(
                        new RecommendationResponse.Place("바른식당", "부천시 역곡동 123", 37.4833, 126.7823, 4.3),
                        new RecommendationResponse.Place("감성카페", "부천시 역곡동 456", 37.4840, 126.7810, 4.5)
                )
        );
    }

    // 프론트에서 location 쿼리 파라미터로 받는 실제 추천 엔드포인트
    @GetMapping
    public RecommendationResponse recommend(@RequestParam("location") String locationName) {
        return recommendationService.recommendPlacesFromLatestSurvey(locationName);
    }
}