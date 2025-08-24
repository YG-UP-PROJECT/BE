// src/main/java/com/example/ygupgoogle/place/PlaceAggregatorService.java
package com.example.ygupgoogle.place;

import com.example.ygupgoogle.place.dto.PlaceSummary;
import com.example.ygupgoogle.place.dto.kakao.KakaoSearchResponse;
import com.example.ygupgoogle.place.dto.google.GooglePlaceDetailsResponse;
import com.example.ygupgoogle.place.dto.google.GoogleTextSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PlaceAggregatorService {

    private final WebClient kakao;
    private final WebClient google;
    private final String googleKey;

    public PlaceAggregatorService(
            // ✅ 루트키 우선, 없으면 app.* 키를 사용
            @Value("${kakao.api.key:${app.kakao.api.key:}}") String kakaoKey,
            @Value("${kakao.api.url:${app.kakao.api.base-url:https://dapi.kakao.com}}") String kakaoBaseUrl,
            @Value("${google.api.key:${app.google.api-key:}}") String googleKey,
            @Value("${google.api.url:https://maps.googleapis.com}") String googleBaseUrl
    ) {
        this.googleKey = Objects.requireNonNullElse(googleKey, "");
        this.kakao = WebClient.builder()
                .baseUrl(kakaoBaseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + Objects.requireNonNullElse(kakaoKey, ""))
                .build();
        this.google = WebClient.builder()
                .baseUrl(googleBaseUrl)
                .build();
    }

    /** Kakao 결과에 Google rating/reviews를 붙여 limit개 반환. Google 실패 시 Kakao만 반환 */
    public List<PlaceSummary> searchWithGoogleRatings(String query, int limit) {
        KakaoSearchResponse kakaoRes = kakao.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/search/keyword.json")
                        .queryParam("query", query)          // uriBuilder가 알아서 인코딩
                        .queryParam("size", limit)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(KakaoSearchResponse.class)
                .onErrorResume(ex -> Mono.empty())          // 401/5xx → empty → null
                .block();

        List<PlaceSummary> out = new ArrayList<>();
        if (kakaoRes == null || kakaoRes.documents() == null) return out;

        for (KakaoSearchResponse.Document d : kakaoRes.documents()) {
            String name        = d.place_name();
            String address     = d.address_name();
            String roadAddress = d.road_address_name();
            double x           = parseOrZero(d.x()); // lng
            double y           = parseOrZero(d.y()); // lat
            String kakaoPlaceUrl = d.place_url();

            Double rating = null;
            Integer total = null;
            List<PlaceSummary.GoogleReview> reviews = List.of();

            // Google enrich (있을 때만)
            if (!googleKey.isBlank()) {
                try {
                    String q = name + " " + (roadAddress != null && !roadAddress.isBlank() ? roadAddress : address);

                    GoogleTextSearchResponse text = google.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/maps/api/place/textsearch/json")
                                    .queryParam("query", q)
                                    .queryParam("language", "ko")
                                    .queryParam("key", googleKey)
                                    .build())
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .bodyToMono(GoogleTextSearchResponse.class)
                            .block();

                    if (text != null && text.results() != null && !text.results().isEmpty()) {
                        String placeId = text.results().get(0).place_id();

                        GooglePlaceDetailsResponse details = google.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/maps/api/place/details/json")
                                        .queryParam("place_id", placeId)
                                        .queryParam("language", "ko")
                                        .queryParam("key", googleKey)
                                        .build())
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .bodyToMono(GooglePlaceDetailsResponse.class)
                                .block();

                        if (details != null && details.result() != null) {
                            rating = details.result().rating();
                            total  = details.result().user_ratings_total();
                            if (details.result().reviews() != null) {
                                reviews = details.result().reviews().stream()
                                        .limit(3)
                                        .map(r -> new PlaceSummary.GoogleReview(
                                                r.author_name(), r.text(), r.rating()))
                                        .toList();
                            }
                        }
                    }
                } catch (Exception ignore) {
                    // 구글 enrich 실패 → 카카오만 반환
                }
            }

            out.add(new PlaceSummary(
                    name, d.category_name(), address, roadAddress, x, y, kakaoPlaceUrl,
                    rating, total, reviews
            ));
        }
        return out;
    }

    private static double parseOrZero(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0d; }
    }
}
