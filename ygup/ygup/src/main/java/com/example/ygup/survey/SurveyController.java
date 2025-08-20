package com.example.ygup.survey;

import com.example.ygup.survey.dto.SurveyCreateRequest;
import com.example.ygup.survey.dto.SurveyCreateResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/surveys")
public class SurveyController {

    private final SurveyService service;

    public SurveyController(SurveyService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SurveyCreateResponse> createSurvey(@RequestBody @Valid SurveyCreateRequest request) {
        SurveyCreateResponse response = service.handleSurvey(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/latest")
    public ResponseEntity<SurveyCreateResponse> getLatestSurvey() {
        SurveyCreateResponse response = service.getLatestSurvey();
        return ResponseEntity.ok(response);
    }
}
