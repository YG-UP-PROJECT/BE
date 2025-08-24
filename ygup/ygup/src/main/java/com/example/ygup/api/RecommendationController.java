package com.example.ygup.api;

import com.example.ygup.dto.RecommendationResponse;
import com.example.ygup.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommend")
@CrossOrigin
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /** GET /api/recommend?location=부천시%20역곡동 */
    @GetMapping
    public RecommendationResponse recommend(@RequestParam("location") String locationName) {
        return recommendationService.recommendPlacesFromLatestSurvey(locationName);
    }
}
