// src/main/java/com/example/ygupgoogle/place/PlaceDetailsController.java
package com.example.ygupgoogle.place.dto;

import com.example.ygup.service.PlaceMappingService;
import com.example.ygupgoogle.place.PlaceAggregatorService;
import com.example.ygupgoogle.place.dto.PlaceDetailsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/place")
public class PlaceDetailsController {

    private final PlaceAggregatorService service;
    private final PlaceMappingService mappingService;

    public PlaceDetailsController(PlaceAggregatorService service, PlaceMappingService mappingService) {
        this.service = service;
        this.mappingService = mappingService;
    }
    @GetMapping
    public ResponseEntity<PlaceDetailsResponse> getDetailsByKakao(
            @RequestParam("kakaoId") String kakaoId
    ) {
        var googleOpt = mappingService.findGoogleIdByKakaoId(kakaoId);
        if (googleOpt.isPresent()) {
            return ResponseEntity.ok(service.getDetailsByGooglePlaceId(googleOpt.get(), kakaoId));
        }

        // 2) 기본정보(이름/주소/좌표)가 캐시돼 있으면 텍스트 서치로 place_id 해석 → 매핑 저장 → 상세
        var basicOpt = mappingService.findByKakaoId(kakaoId);
        if (basicOpt.isPresent()) {
            var basic = basicOpt.get();
            String resolved = service.resolveGooglePlaceIdByText(
                    basic.getName(), basic.getAddress(), basic.getLat(), basic.getLng()
            );
            if (resolved != null && !resolved.isBlank()) {
                mappingService.upsert(
                        kakaoId, resolved,
                        basic.getName(), basic.getAddress(), basic.getLat(), basic.getLng()
                );
                return ResponseEntity.ok(service.getDetailsByGooglePlaceId(resolved, kakaoId));
            }
        }
        // 매핑이 아직 없다면(검색 리스트를 안 거쳤거나 캐시 미스),
        // 구글ID 없이도 최소한 Kakao 정보만으로 PlaceDetailsResponse 뼈대는 내려줄 수 있음.
        // 여기서는 구글 상세가 없으면 name/address/rating이 null일 수 있음.
        return ResponseEntity.ok(
                googleOpt
                        .map(googleId -> service.getDetailsByGooglePlaceId(googleId, kakaoId))
                        .orElseGet(() -> new PlaceDetailsResponse(
                                kakaoId, null, null, null,
                                null, null, List.of(), List.of(), List.of()
                        ))
        );
    }
    /**
     * 구글 place_id 기반 상세조회 API
     * 예) GET /api/place/ChIJR4wDlHpjezURPeWmokx1Aac
     *     GET /api/place/ChIJR4wDlHpjezURPeWmokx1Aac?kakaoId=17813115
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlaceDetailsResponse> getDetails(
            @PathVariable("id") String googlePlaceId,
            @RequestParam(value = "kakaoId", required = false) String kakaoId
    ) {
        return ResponseEntity.ok(service.getDetailsByGooglePlaceId(googlePlaceId, kakaoId));
    }

}
