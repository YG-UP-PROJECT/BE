package com.example.ygup.service;

import com.example.ygup.dto.RecommendationResponse.Place;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class KakaoMapService {

    private final WebClient webClient;

    @Value("${kakao.api.key:}")
    private String kakaoApiKey;

    public KakaoMapService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://dapi.kakao.com").build();
    }

    public List<Place> searchPlaces(String location, String keywords) {
        List<Place> results = new ArrayList<>();
        String[] keywordList = keywords.split(",");

        for (String keyword : keywordList) {
            String query = location + " " + keyword.trim();

            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/search/keyword.json")
                            .queryParam("query", query)
                            .build())
                    .header("Authorization", "KakaoAK " + kakaoApiKey)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();


            results.add(new Place(keyword.trim() + " 맛집", location + " 어딘가", 37.5, 126.8, 4.5));
        }

        return results;
    }
}

