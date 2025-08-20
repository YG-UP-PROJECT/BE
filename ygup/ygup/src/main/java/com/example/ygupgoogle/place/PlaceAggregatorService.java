package com.example.ygupgoogle.place;

import com.example.ygupgoogle.place.dto.PlaceSummary;
import com.example.ygupgoogle.place.dto.kakao.KakaoSearchResponse;
import com.example.ygupgoogle.place.dto.google.GooglePlaceDetailsResponse;
import com.example.ygupgoogle.place.dto.google.GoogleTextSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PlaceAggregatorService {

    private final WebClient webClient;
    private final String kakaoKey;
    private final String googleKey;

    public PlaceAggregatorService(
            WebClient.Builder builder,
            @Value("${app.kakao.rest-api-key}") String kakaoKey,
            @Value("${app.google.api-key}") String googleKey
    ) {
        this.webClient = builder.build();
        this.kakaoKey = kakaoKey;
        this.googleKey = googleKey;
    }

    public List<PlaceSummary> searchWithGoogleRatings(String query, int limit) {
        // 1) 카카오 검색
        var kakaoDocs = kakaoKeywordSearch(query);

        // 상위 N개
        var top = kakaoDocs.stream().limit(limit).toList();

        List<PlaceSummary> result = new ArrayList<>();

        for (var d : top) {
            // 2) 구글 텍스트 검색으로 place_id 매칭 (이름 + 주소, 위치 바이어스)
            Double x = parseOrNull(d.x());
            Double y = parseOrNull(d.y());

            String textQuery = d.place_name() + " " + safe(d.road_address_name().isBlank() ? d.address_name() : d.road_address_name());
            var googleMatch = googleTextSearch(textQuery, y, x); // lat(y), lng(x)

            Double rating = null;
            Integer ratingsTotal = null;
            List<PlaceSummary.GoogleReview> reviews = List.of();

            if (googleMatch != null && !googleMatch.results().isEmpty()) {
                String placeId = googleMatch.results().get(0).place_id();

                // 3) 상세조회로 평점/리뷰
                var details = googlePlaceDetails(placeId);
                if (details != null && details.result() != null) {
                    rating = details.result().rating();
                    ratingsTotal = details.result().user_ratings_total();
                    if (details.result().reviews() != null) {
                        reviews = details.result().reviews().stream()
                                .sorted(Comparator.comparingInt((GooglePlaceDetailsResponse.Result.Review r) -> r.rating() == null ? 0 : r.rating()).reversed())
                                .limit(3)
                                .map(r -> new PlaceSummary.GoogleReview(r.author_name(), r.text(), r.rating()))
                                .toList();
                    }
                }
            }

            result.add(new PlaceSummary(
                    d.place_name(),
                    d.category_name(),
                    d.address_name(),
                    d.road_address_name(),
                    parseOrZero(d.x()),
                    parseOrZero(d.y()),
                    d.place_url(),
                    rating,
                    ratingsTotal,
                    reviews
            ));
        }

        return result;
    }

    private List<KakaoSearchResponse.Document> kakaoKeywordSearch(String query) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("query", query);
        params.add("size", "15");

        KakaoSearchResponse resp = webClient.get()
                .uri(uri -> uri
                        .scheme("https")
                        .host("dapi.kakao.com")
                        .path("/v2/local/search/keyword.json")
                        .queryParams(params)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoKey)
                .retrieve()
                .bodyToMono(KakaoSearchResponse.class)
                .block();

        return resp == null || resp.documents() == null ? List.of() : resp.documents();
    }

    private GoogleTextSearchResponse googleTextSearch(String textQuery, Double lat, Double lng) {
        // 구글 구버전 Web Service (Text Search)
        // https://maps.googleapis.com/maps/api/place/textsearch/json
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("query", textQuery);
        params.add("key", googleKey);
        if (lat != null && lng != null) {
            params.add("location", lat + "," + lng);
            params.add("radius", "1500"); // 1.5km 반경
        }
        params.add("language", "ko");

        return webClient.get()
                .uri(uri -> uri
                        .scheme("https")
                        .host("maps.googleapis.com")
                        .path("/maps/api/place/textsearch/json")
                        .queryParams(params)
                        .build())
                .retrieve()
                .bodyToMono(GoogleTextSearchResponse.class)
                .block();
    }

    private GooglePlaceDetailsResponse googlePlaceDetails(String placeId) {
        // https://maps.googleapis.com/maps/api/place/details/json
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("place_id", placeId);
        params.add("fields", "rating,user_ratings_total,reviews"); // 필요 필드만
        params.add("key", googleKey);
        params.add("language", "ko");

        return webClient.get()
                .uri(uri -> uri
                        .scheme("https")
                        .host("maps.googleapis.com")
                        .path("/maps/api/place/details/json")
                        .queryParams(params)
                        .build())
                .retrieve()
                .bodyToMono(GooglePlaceDetailsResponse.class)
                .block();
    }

    private static Double parseOrNull(String s) {
        try { return s == null ? null : Double.parseDouble(s); } catch (Exception e) { return null; }
    }
    private static double parseOrZero(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0d; }
    }
    private static String safe(String s) {
        if (s == null) return "";
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
