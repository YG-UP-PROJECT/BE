package com.example.ygupgoogle.place;

import com.example.ygup.service.PlaceMappingService;
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
import com.example.ygupgoogle.place.dto.PlaceDetailsResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.http.ResponseEntity;
@Service
public class PlaceAggregatorService {

    private final WebClient webClient;
    private final String kakaoKey;
    private final String googleKey;
    private final PlaceMappingService placeMappingService;

    public PlaceAggregatorService(
            WebClient.Builder builder,
            @Value("${app.kakao.api.key}") String kakaoKey,   // 수정
            @Value("${app.google.api-key}") String googleKey,  // 수정
            PlaceMappingService placeMappingService
    ) {
        this.webClient = builder.build();
        this.kakaoKey = kakaoKey;
        this.googleKey = googleKey;
        this.placeMappingService = placeMappingService;
    }
    public ResponseEntity<byte[]> fetchPhoto(String photoRef, int maxWidth) {
        return webClient.get()
                .uri(uri -> uri
                        .scheme("https")
                        .host("maps.googleapis.com")
                        .path("/maps/api/place/photo")
                        .queryParam("maxwidth", maxWidth)
                        .queryParam("photo_reference", photoRef)
                        .queryParam("key", googleKey)
                        .build())
                .exchangeToMono(resp -> resp.toEntity(byte[].class))
                .block();
    }


    public byte[] fetchPhotoBytes(String photoRef, int maxWidth) {
        return webClient.get()
                .uri(uri -> uri
                        .scheme("https")
                        .host("maps.googleapis.com")
                        .path("/maps/api/place/photo")
                        .queryParam("maxwidth", maxWidth)
                        .queryParam("photo_reference", photoRef)
                        .queryParam("key", googleKey)
                        .build())
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }

    public PlaceDetailsResponse getDetailsByGooglePlaceId(String googlePlaceId, String kakaoIdOpt) {
        var details = googlePlaceDetails(googlePlaceId);
        if (details == null || details.result() == null) {
            return new PlaceDetailsResponse(
                    googlePlaceId, kakaoIdOpt, null, null,
                    null, null, List.of(), List.of(), List.of()
            );
        }

        var res = details.result();

        // 리뷰 상위 3개 (평점 높은 순)
        List<PlaceDetailsResponse.Review> reviews = List.of();
        if (res.reviews() != null) {
            reviews = res.reviews().stream()
                    .sorted(Comparator.comparingInt(
                            (GooglePlaceDetailsResponse.Result.Review r) -> r.rating() == null ? 0 : r.rating()
                    ).reversed())
                    .limit(3)
                    .map(r -> new PlaceDetailsResponse.Review(r.author_name(), r.rating(), r.text()))
                    .toList();
        }

        // 사진 (최대 6장) + 출처
        java.util.List<String> photoUrls = new java.util.ArrayList<>();
        java.util.List<String> attributions = new java.util.ArrayList<>();
        if (res.photos() != null) {
            res.photos().stream()
                    .limit(6)
                    .forEach(p -> {
                        photoUrls.add(buildPhotoProxyUrl(p.photo_reference(), 800));
                        if (p.html_attributions() != null) attributions.addAll(p.html_attributions());
                    });
        }

        return new PlaceDetailsResponse(
                kakaoIdOpt,
                googlePlaceId,
                res.name(),
                res.formatted_address(),
                res.rating(),
                res.user_ratings_total(),
                reviews,
                photoUrls,
                attributions
        );
    }
    public String resolveGooglePlaceIdByText(String name, String address, Double lat, Double lng) {
        if ((name == null || name.isBlank()) && (address == null || address.isBlank())) return null;
        String textQuery = ((name == null) ? "" : name) + " " + ((address == null) ? "" : address);
        var ts = googleTextSearch(textQuery.trim(), lat, lng);
        if (ts != null && ts.results() != null && !ts.results().isEmpty()) {
            return ts.results().get(0).place_id();
        }
        return null;
    }

    public List<PlaceSummary> searchWithGoogleRatings(String query, int limit) {
        var kakaoDocs = kakaoKeywordSearch(query);
        var top = kakaoDocs.stream().limit(limit).toList();
        List<PlaceSummary> result = new ArrayList<>();

        for (var d : top) {
            Double x = parseOrNull(d.x());
            Double y = parseOrNull(d.y());

            String textQuery = d.place_name() + " " +
                    safe(d.road_address_name() == null || d.road_address_name().isBlank()
                            ? d.address_name()
                            : d.road_address_name());
            var googleMatch = googleTextSearch(textQuery, y, x);

            Double rating = null;
            Integer ratingsTotal = null;
            List<PlaceSummary.GoogleReview> reviews = List.of();
            List<String> photoProxyUrls = new ArrayList<>();
            List<String> attributions = new ArrayList<>();

            // 1) 텍스트 검색 결과에서 사진 ref 뽑기 (빠르고 쿼터 절약)
            List<GoogleTextSearchResponse.Result.Photo> textPhotos = List.of();
            if (googleMatch != null && !googleMatch.results().isEmpty()) {
                var first = googleMatch.results().get(0);
                if (first.photos() != null) textPhotos = first.photos();
            }

            String placeId = (googleMatch != null && !googleMatch.results().isEmpty())
                    ? googleMatch.results().get(0).place_id()
                    : null;

            String googlePlaceId = placeId;
            String kakaoId = d.id(); // KakaoSearchResponse.Document에 id 필드 추가돼 있어야 함

            // ★ 매핑 캐시 저장 (검색 시점에 확보된 정보로 갱신)
            placeMappingService.upsert(
                    kakaoId,
                    googlePlaceId,
                    d.place_name(),
                    (d.road_address_name() != null && !d.road_address_name().isBlank()) ? d.road_address_name() : d.address_name(),
                    parseOrNull(d.y()), // lat
                    parseOrNull(d.x())  // lng
            );


            // 2) 상세 조회 (평점/리뷰/추가사진)
            if (placeId != null) {
                var details = googlePlaceDetails(placeId); // 아래 fields에 photos 추가됨
                if (details != null && details.result() != null) {
                    rating = details.result().rating();
                    ratingsTotal = details.result().user_ratings_total();

                    if (details.result().reviews() != null) {
                        reviews = details.result().reviews().stream()
                                .sorted(Comparator.comparingInt((GooglePlaceDetailsResponse.Result.Review r)
                                        -> r.rating() == null ? 0 : r.rating()).reversed())
                                .limit(3)
                                .map(r -> new PlaceSummary.GoogleReview(r.author_name(), r.text(), r.rating()))
                                .toList();
                    }

                    // 사진: textSearch + details 합쳐 상위 3장
                    var mergedPhotos = new ArrayList<GooglePlaceDetailsResponse.Result.Photo>();
                    // 텍스트 검색 사진을 details.Photo로 억지 매핑
                    for (var p : textPhotos) {
                        mergedPhotos.add(new GooglePlaceDetailsResponse.Result.Photo(
                                p.photo_reference(), p.html_attributions(), p.width(), p.height()
                        ));
                    }
                    if (details.result().photos() != null) mergedPhotos.addAll(details.result().photos());

                    mergedPhotos.stream()
                            .map(p -> {
                                if (p.html_attributions() != null) attributions.addAll(p.html_attributions());
                                return buildPhotoProxyUrl(p.photo_reference(), 800);
                            })
                            .distinct()
                            .limit(1)
                            .forEach(photoProxyUrls::add);
                }
            }

            result.add(new PlaceSummary(
                    kakaoId,
                    googlePlaceId,

                    d.place_name(),
                    d.category_name(),
                    d.address_name(),
                    d.road_address_name(),
                    parseOrZero(d.x()),
                    parseOrZero(d.y()),
                    d.place_url(),
                    rating,
                    ratingsTotal,
                    reviews,
                    photoProxyUrls,
                    attributions
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
        params.add("fields", "name,formatted_address,rating,user_ratings_total,reviews,photos");
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
        return (s == null) ? "" : s.trim();
    }

    private String buildPhotoProxyUrl(String photoRef, int maxWidth) {
        return "/places/photo?ref=" + URLEncoder.encode(photoRef, StandardCharsets.UTF_8)
                + "&maxWidth=" + maxWidth;
    }

}
