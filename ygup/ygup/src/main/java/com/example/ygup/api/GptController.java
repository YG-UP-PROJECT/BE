package com.example.ygup.api;

import com.example.ygup.dto.KeywordResponse;
import com.example.ygup.dto.PreferenceRequest;
import com.example.ygup.service.GptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gpt")
@CrossOrigin // 프론트 로컬 테스트 위해 허용(원하면 도메인 지정 가능)
public class GptController {

    private final GptService gptService;

    public GptController(GptService gptService) {
        this.gptService = gptService;
    }

    // 건강검진용 간단 체크
    @GetMapping("/health")
    public String health() { return "ok"; }

    @PostMapping("/keywords")
    public ResponseEntity<KeywordResponse> keywords(@RequestBody PreferenceRequest req) {
        KeywordResponse res = gptService.generateKeywords(req);
        return ResponseEntity.ok(res);
    }
}

