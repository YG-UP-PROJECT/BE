package com.example.ygup.api;

import com.example.ygup.dto.KeywordResponse;
import com.example.ygup.dto.PreferenceRequest;
import com.example.ygup.service.GptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gpt")
@CrossOrigin
public class GptController {

    private final GptService gptService;

    public GptController(GptService gptService) {
        this.gptService = gptService;
    }

    @GetMapping("/health")
    public String health() {
        return "ok";
    }

    @PostMapping("/keywords")
    public ResponseEntity<KeywordResponse> keywords(@RequestBody PreferenceRequest req) {
        KeywordResponse res = gptService.generateKeywords(req);
        return ResponseEntity.ok(res);
    }
}