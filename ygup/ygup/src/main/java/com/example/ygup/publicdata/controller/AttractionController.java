package com.example.ygup.publicdata.controller;

import com.example.ygup.publicdata.dto.*;
import com.example.ygup.publicdata.entity.Attraction;
import com.example.ygup.publicdata.service.AttractionService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attractions")
@Validated // ✅ 파라미터 검증 활성화
public class AttractionController {

    private final AttractionService service;

    public AttractionController(AttractionService service) {
        this.service = service;
    }

    // ---- 외부 API 프락시 ----
    @GetMapping("/search")
    public ResponseEntity<PageResponse<AttractionSummaryDto>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        // 공백/따옴표 방어
        String k = sanitize(keyword);
        return ResponseEntity.ok(service.searchKeyword(k, page, size));
    }

    @GetMapping("/nearby")
    public ResponseEntity<PageResponse<AttractionSummaryDto>> nearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "800") int radius,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(service.nearby(lat, lng, radius, page, size));
    }

    // 상세 + (선택) contentTypeId
    @GetMapping("/{contentId}")
    public ResponseEntity<AttractionDetailDto> detail(
            @PathVariable long contentId,
            @RequestParam(value = "contentTypeId", required = false) Integer contentTypeId
    ) {
        return ResponseEntity.ok(service.detail(contentId, contentTypeId));
    }

    @PostMapping("/sync/{contentId}")
    public ResponseEntity<AttractionDetailDto> sync(@PathVariable long contentId) {
        return ResponseEntity.ok(service.syncFromExternal(contentId));
    }

    // ---- 로컬 DB CRUD ----
    @GetMapping
    public ResponseEntity<List<Attraction>> all() { return ResponseEntity.ok(service.all()); }

    @GetMapping("/local/{contentId}")
    public ResponseEntity<Attraction> one(@PathVariable long contentId) { return ResponseEntity.ok(service.get(contentId)); }

    @PostMapping
    public ResponseEntity<Attraction> create(@RequestBody Attraction a) { return ResponseEntity.ok(service.create(a)); }

    @PutMapping("/local/{contentId}")
    public ResponseEntity<Attraction> update(@PathVariable long contentId, @RequestBody Attraction a) {
        return ResponseEntity.ok(service.update(contentId, a));
    }

    @DeleteMapping("/local/{contentId}")
    public ResponseEntity<Void> delete(@PathVariable long contentId) {
        service.delete(contentId);
        return ResponseEntity.noContent().build();
    }

    // ---- 내부 ----
    private static String sanitize(String s) {
        if (s == null) return null;
        s = s.trim();
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            s = s.substring(1, s.length() - 1).trim();
        }
        return s;
    }
}
