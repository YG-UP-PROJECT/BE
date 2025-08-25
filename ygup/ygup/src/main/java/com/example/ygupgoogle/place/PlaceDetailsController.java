// src/main/java/com/example/ygupgoogle/place/PlaceDetailsController.java
package com.example.ygupgoogle.place;

import com.example.ygup.service.PlaceMappingService;
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
            // kakaoId 대소문자 실수 방지: 둘 다 허용
            @RequestParam(value = "kakaoId", required = false) String kakaoIdCamel,
            @RequestParam(value = "kakaoid", required = false) String kakaoIdLower,

            // ▼ 프론트가 주면 사용하는 옵션 파라미터 (없어도 동작)
            @RequestParam(value = "name", required = false) String nameOpt,
            @RequestParam(value = "address", required = false) String addressOpt,
            @RequestParam(value = "lat", required = false) Double latOpt,
            @RequestParam(value = "lng", required = false) Double lngOpt,
            @RequestParam(value = "placeUrl", required = false) String kakaoPlaceUrlOpt
    ) {
        String kakaoId = (kakaoIdCamel != null && !kakaoIdCamel.isBlank()) ? kakaoIdCamel : kakaoIdLower;
        if (kakaoId == null || kakaoId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // 0) 기존 매핑이 있으면 바로 상세
        var googleOpt = mappingService.findGoogleIdByKakaoId(kakaoId);
        if (googleOpt.isPresent()) {
            return ResponseEntity.ok(service.getDetailsByGooglePlaceId(googleOpt.get(), kakaoId));
        }

        // 1) DB에 기본정보가 있으면 그걸로 place_id 해석 → 저장 → 상세
        var basicOpt = mappingService.findByKakaoId(kakaoId);
        if (basicOpt.isPresent()) {
            var b = basicOpt.get();
            String resolved = service.resolveGooglePlaceIdByText(b.getName(), b.getAddress(), b.getLat(), b.getLng());
            if (resolved != null && !resolved.isBlank()) {
                mappingService.upsert(kakaoId, resolved, b.getName(), b.getAddress(), b.getLat(), b.getLng());
                return ResponseEntity.ok(service.getDetailsByGooglePlaceId(resolved, kakaoId));
            }
        }

        // 2) 프론트가 넘겨준 기본정보가 있으면 그걸로 해석 → 저장 → 상세
        boolean hasFrontBasic =
                (nameOpt != null && !nameOpt.isBlank()) ||
                        (addressOpt != null && !addressOpt.isBlank());
        if (hasFrontBasic) {
            String resolved = service.resolveGooglePlaceIdByText(nameOpt, addressOpt, latOpt, lngOpt);
            if (resolved != null && !resolved.isBlank()) {
                mappingService.upsert(kakaoId, resolved, nameOpt, addressOpt, latOpt, lngOpt);
                return ResponseEntity.ok(service.getDetailsByGooglePlaceId(resolved, kakaoId));
            }
        }

        // 3) (선택) placeUrl만으로의 후속 처리 훅 자리 (스크래핑 비권장 → 필요 시 별도 어댑터)

        // 그래도 못 찾으면 최소 스켈레톤 반환
        return ResponseEntity.ok(new PlaceDetailsResponse(
                kakaoId, null, null, null,
                null, null, List.of(), List.of(), List.of()
        ));
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
