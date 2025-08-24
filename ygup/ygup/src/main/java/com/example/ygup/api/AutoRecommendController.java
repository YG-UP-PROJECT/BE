// src/main/java/com/example/ygup/api/AutoRecommendController.java
package com.example.ygup.api;

import com.example.ygup.dto.*;
import com.example.ygup.dto.IncomingSurveyRequest;
import com.example.ygup.dto.KeywordResponse;
import com.example.ygup.dto.PreferenceRequest;
import com.example.ygupgoogle.place.PlaceAggregatorService;
import com.example.ygupgoogle.place.dto.PlaceSummary;
import com.example.ygup.service.GptService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/recommend")
public class AutoRecommendController {

    private final GptService gptService;
    private final PlaceAggregatorService placeAgg;

    public AutoRecommendController(GptService gptService, PlaceAggregatorService placeAgg) {
        this.gptService = gptService;
        this.placeAgg = placeAgg;
    }

    @PostMapping("/auto")
    public ResponseEntity<AutoRecommendationResponse> recommendAuto(
            @RequestBody @Valid IncomingSurveyRequest in
    ) {
        String loc = (in.location() == null || in.location().isBlank())
                ? "부천시 역곡동" : in.location().trim();

        // 1) GPT 키워드 생성
        PreferenceRequest req = new PreferenceRequest();
        req.setMood(in.mood());
        req.setFoodStyle(in.foodStyle());
        req.setDiningStyle(in.diningStyle());
        req.setTimeSlot(in.timeSlot());
        req.setWeather(in.weather());
        req.setLocation(loc);

        KeywordResponse kw = gptService.generateKeywords(req);
        List<String> keywords = kw.getKeywords();
        if (keywords == null) keywords = List.of();

        // 2) 키워드별 검색(구글 평점/사진 포함)
        final int limitPerKeyword = 5;
        Map<String, PlaceCard> dedup = new LinkedHashMap<>(); // kakaoId 기준 중복 제거

        for (String k : keywords) {
            String kwTrim = k == null ? "" : k.trim();
            if (kwTrim.isEmpty()) continue;

            // 위치어 중복 제거 (예: "부천시 역곡동 역곡 카페" 예방)
            String kwNoLoc = kwTrim.replaceFirst("^" + Pattern.quote(loc) + "\\s*", "");
            String query = loc + " " + kwNoLoc;

            List<PlaceSummary> list = placeAgg.searchWithGoogleRatings(query, limitPerKeyword);
            for (PlaceSummary s : list) {
                String kakaoId = s.kakaoId();
                if (kakaoId == null || kakaoId.isBlank()) continue;
                if (dedup.containsKey(kakaoId)) continue;

                String address = (s.roadAddress() != null && !s.roadAddress().isBlank())
                        ? s.roadAddress() : s.address();

                // ★ photoUrls 포함해서 카드화
                PlaceCard card = new PlaceCard(
                        kakaoId,
                        s.name(),
                        address,
                        s.kakaoY(),            // lat
                        s.kakaoX(),            // lng
                        s.googleRating(),      // 평점
                        s.kakaoPlaceUrl(),
                        s.photoUrls() == null ? List.of() : s.photoUrls()
                );
                dedup.put(kakaoId, card);
            }
        }

        List<PlaceCard> places = new ArrayList<>(dedup.values());
        return ResponseEntity.ok(new AutoRecommendationResponse(loc, keywords, places));
    }
}
