package com.example.ygup.service;

import com.example.ygup.dto.PreferenceRequest;
import com.example.ygup.dto.KeywordResponse;
import com.example.ygup.entity.SurveyEntity;
import com.example.ygup.survey.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class GptService {

    private final WebClient webClient;
    private final SurveyRepository surveyRepository;
    private final String geminiApiKey;

    @Autowired
    public GptService(WebClient.Builder webClientBuilder,
                      SurveyRepository surveyRepository,
                      @Value("${gemini.api-key}") String geminiApiKey) {
        this.webClient = webClientBuilder.baseUrl("https://generativelanguage.googleapis.com/").build();
        this.surveyRepository = surveyRepository;
        this.geminiApiKey = geminiApiKey;
    }

    public KeywordResponse generateKeywords(PreferenceRequest request) {
        String prompt = generatePromptFromRequest(request);

        String response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/models/gemini-1.5-flash:generateContent")
                        .queryParam("key", geminiApiKey)
                        .build())
                .header("Content-Type", "application/json")
                .bodyValue("{\"contents\":[{\"parts\":[{\"text\":\"" + prompt + "\"}]}]}")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        List<String> keywords = extractKeywords(response);

        SurveyEntity entity = SurveyEntity.builder()
                .mood(request.getMood())
                .foodStyle(request.getFoodStyle())
                .diningStyle(request.getDiningStyle())
                .timeSlot(request.getTimeSlot())
                .weather(request.getWeather())
                .tempC(null)
                .keywords(String.join(",", keywords))
                .build();

        surveyRepository.save(entity);

        return new KeywordResponse(keywords, prompt);
    }

    private List<String> extractKeywords(String response) {
        return List.of("감성카페", "건강식", "비 오는 날 분위기 좋은 식당");
    }

    private String generatePromptFromRequest(PreferenceRequest req) {
        return "기분이 " + req.getMood() + "하고, 날씨는 " + req.getWeather() +
                "일 때 어울리는 맛집 키워드를 추천해줘.";
    }
}
