// src/main/java/com/example/ygup/service/RecommendationService.java
package com.example.ygup.service;

import com.example.ygup.dto.RecommendationResponse;
import com.example.ygup.dto.RecommendationResponse.Place;
import com.example.ygup.entity.SurveyEntity;
import com.example.ygup.survey.SurveyRepository;
import com.example.ygupgoogle.place.PlaceAggregatorService;
import com.example.ygupgoogle.place.dto.PlaceSummary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationService {

    private final SurveyRepository surveyRepository;
    private final KakaoMapService kakaoMapService;
    private final PlaceAggregatorService aggregator;

    public RecommendationService(
            SurveyRepository surveyRepository,
            KakaoMapService kakaoMapService,
            PlaceAggregatorService aggregator
    ) {
        this.surveyRepository = surveyRepository;
        this.kakaoMapService = kakaoMapService;
        this.aggregator = aggregator;
    }

    /** 최신 설문 기반 추천 + 폴백(카카오) */
    public RecommendationResponse recommendPlacesFromLatestSurvey(String locationName) {
        SurveyEntity latestSurvey = surveyRepository.findTopByOrderByIdDesc();

        String keywords = (latestSurvey != null ? latestSurvey.getKeywords() : "");
        String q = ((locationName == null ? "" : locationName.trim()) + " " +
                (keywords == null ? "" : keywords.replace(",", " ").replace("  ", " ").trim())).trim();

        // 1) 집계(카카오 + 구글 rating/review)
        List<PlaceSummary> enriched = aggregator.searchWithGoogleRatings(q, 5);

        List<Place> out = new ArrayList<>();
        if (enriched != null && !enriched.isEmpty()) {
            for (PlaceSummary s : enriched) {
                Place p = new Place();
                p.setName(s.name());
                p.setAddress(s.roadAddress() != null && !s.roadAddress().isBlank() ? s.roadAddress() : s.address());
                p.setLat(s.kakaoY());
                p.setLng(s.kakaoX());
                p.setRating(s.googleRating()); // 구글 별점
                out.add(p);
            }
            return new RecommendationResponse(locationName, out);
        }

        // 2) 폴백: 카카오 전용 검색
        out = kakaoMapService.searchPlaces(locationName, keywords);
        return new RecommendationResponse(locationName, out);
    }
}
