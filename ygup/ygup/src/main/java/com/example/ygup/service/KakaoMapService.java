// src/main/java/com/example/ygup/service/KakaoMapService.java
package com.example.ygup.service;

import com.example.ygup.dto.RecommendationResponse;
import com.example.ygup.dto.RecommendationResponse.Place;
import com.example.ygupgoogle.place.dto.kakao.KakaoSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class KakaoMapService {

    private final WebClient webClient;

    public KakaoMapService(
            // ✅ 루트키 우선, 없으면 app.* 키 사용
            @Value("${kakao.api.key:${app.kakao.api.key:}}") String restApiKey,
            @Value("${kakao.api.url:${app.kakao.api.base-url:https://dapi.kakao.com}}") String baseUrl
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + Objects.requireNonNullElse(restApiKey, ""))
                .build();
    }

    /** 위치명 + 키워드 CSV로 카카오 키워드 검색 (평점은 Kakao 미제공 → null) */
    public List<Place> searchPlaces(String locationName, String keywordCsv) {
        String q = ((locationName == null ? "" : locationName.trim()) + " " +
                (keywordCsv == null ? "" : keywordCsv.replace(",", " ").replace("  ", " ").trim())).trim();

        KakaoSearchResponse res = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/search/keyword.json")
                        .queryParam("query", q)
                        .queryParam("size", 5)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(KakaoSearchResponse.class)
                .onErrorResume(ex -> Mono.empty())
                .block();

        List<Place> results = new ArrayList<>();
        if (res == null || res.documents() == null) return results;

        for (KakaoSearchResponse.Document d : res.documents()) {
            Place p = new Place();
            p.setName(d.place_name());
            p.setAddress(d.road_address_name() != null && !d.road_address_name().isBlank() ?
                    d.road_address_name() : d.address_name());
            // Kakao: x=lng, y=lat
            try { p.setLng(Double.parseDouble(d.x())); } catch (Exception ignore) {}
            try { p.setLat(Double.parseDouble(d.y())); } catch (Exception ignore) {}
            p.setRating(null); // Kakao는 별점 미제공
            results.add(p);
        }
        return results;
    }
}
