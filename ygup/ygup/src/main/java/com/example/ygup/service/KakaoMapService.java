// src/main/java/com/example/ygup/service/KakaoMapService.java
package com.example.ygup.service;

import com.example.ygup.dto.RecommendationResponse.Place;
import com.example.ygupgoogle.place.dto.kakao.KakaoSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class KakaoMapService {

    private final WebClient webClient;

    @Value("${app.kakao.api.key:}")
    private String kakaoApiKey;

    public KakaoMapService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://dapi.kakao.com").build();
    }

    public List<Place> searchPlaces(String location, String keywordsCsv) {
        List<Place> results = new ArrayList<>();
        String[] keywordList = keywordsCsv.split(",");

        for (String k : keywordList) {
            String query = (location == null || location.isBlank())
                    ? k.trim()
                    : (location.trim() + " " + k.trim());

            KakaoSearchResponse resp = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/search/keyword.json")
                            .queryParam("query", query)
                            .queryParam("size", 5)
                            .build())
                    .header("Authorization", "KakaoAK " + kakaoApiKey)
                    .retrieve()
                    .bodyToMono(KakaoSearchResponse.class)
                    .block();

            if (resp == null || resp.documents() == null) continue;

            for (var d : resp.documents()) {
                double lng = parseOrZero(d.x()); // 경도
                double lat = parseOrZero(d.y()); // 위도
                String addr = (d.road_address_name() != null && !d.road_address_name().isBlank())
                        ? d.road_address_name()
                        : d.address_name();

                results.add(new Place(
                        d.place_name(),
                        addr,
                        lat,
                        lng,
                        null // 카카오 응답엔 별점 없음. (평점이 필요하면 PlaceAggregatorService 사용)
                ));
            }
        }
        return results;
    }

    private static double parseOrZero(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0d; }
    }
}
