// src/main/java/com/example/ygupgoogle/place/PlaceDetailsController.java
package com.example.ygupgoogle.place;

import com.example.ygupgoogle.place.dto.PlaceDetailsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/place")
public class PlaceDetailsController {

    private final PlaceAggregatorService service;

    public PlaceDetailsController(PlaceAggregatorService service) {
        this.service = service;
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
