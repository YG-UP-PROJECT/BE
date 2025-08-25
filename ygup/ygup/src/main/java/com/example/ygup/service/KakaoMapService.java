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
    private final PlaceMappingService placeMappingService; // ★ 필드 추가

    @Value("${app.kakao.api.key:}")
    private String kakaoApiKey;

    // ★ 생성자 주입: PlaceMappingService 추가
    public KakaoMapService(WebClient.Builder webClientBuilder,
                           PlaceMappingService placeMappingService) {
        this.webClient = webClientBuilder.baseUrl("https://dapi.kakao.com").build();
        this.placeMappingService = placeMappingService;
    }

    public List<Place> searchPlaces(String location, String keywordsCsv) {
        List<Place> results = new ArrayList<>();

        if (keywordsCsv == null || keywordsCsv.isBlank()) {
            return results; // 키워드 없으면 빈 리스트
        }

        String[] keywordList = keywordsCsv.split(",");

        for (String k : keywordList) {
            String kw = (k == null) ? "" : k.trim();
            if (kw.isEmpty()) continue;

            String loc = (location == null) ? "" : location.trim();
            String query = loc.isEmpty() ? kw : (loc + " " + kw);

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

                // ★ 목록 단계에서 최소 캐시: (구글 place_id는 null이어도 OK)
                placeMappingService.upsert(
                        d.id(), null, d.place_name(), addr, lat, lng
                );

                results.add(new Place(
                        d.id(),
                        d.place_name(),
                        addr,
                        lat,
                        lng,
                        null, // 카카오는 평점 없음
                        d.place_url()
                ));
            }
        }
        return results;
    }

    private static double parseOrZero(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0d; }
    }
}
