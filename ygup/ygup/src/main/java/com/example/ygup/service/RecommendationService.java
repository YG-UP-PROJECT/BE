package com.example.ygup.service;

import com.example.ygup.dto.RecommendationResponse;
import com.example.ygup.dto.RecommendationResponse.Place;
import com.example.ygup.entity.SurveyEntity;
import com.example.ygup.survey.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationService {

    private final SurveyRepository surveyRepository;
    private final KakaoMapService kakaoMapService;

    @Autowired
    public RecommendationService(SurveyRepository surveyRepository, KakaoMapService kakaoMapService) {
        this.surveyRepository = surveyRepository;
        this.kakaoMapService = kakaoMapService;
    }

    public RecommendationResponse recommendPlacesFromLatestSurvey(String locationNameFromFrontend) {
        SurveyEntity latestSurvey = surveyRepository.findTopByOrderByIdDesc();
        if (latestSurvey == null) {
            throw new RuntimeException("설문 데이터가 없습니다.");
        }

        String keywords = latestSurvey.getKeywords();

        List<Place> places = kakaoMapService.searchPlaces(locationNameFromFrontend, keywords);
        return new RecommendationResponse(locationNameFromFrontend, places);
    }
}