package com.example.ygupgoogle.place;

import com.example.ygupgoogle.place.dto.PlaceSummary;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/places")
public class PlaceController {

    private final PlaceAggregatorService service;

    public PlaceController(PlaceAggregatorService service) {
        this.service = service;
    }

    // 예: /places/search?query=역곡 고깃집&limit=3
    @GetMapping("/search")
    public ResponseEntity<List<PlaceSummary>> search(
            @RequestParam @NotBlank String query,
            @RequestParam(defaultValue = "3") @Min(1) @Max(10) int limit
    ) {
        return ResponseEntity.ok(service.searchWithGoogleRatings(query, limit));
    }
}
