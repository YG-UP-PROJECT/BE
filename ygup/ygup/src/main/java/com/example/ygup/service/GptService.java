package com.example.ygup.service;

import com.example.ygup.dto.PreferenceRequest;
import com.example.ygup.dto.KeywordResponse;
import com.example.ygup.entity.SurveyEntity;
import com.example.ygup.survey.SurveyRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * OpenAI Chat Completions 사용해 키워드 3~5개 생성.
 * 응답은 ["키워드1","키워드2"] 형태의 JSON 배열 문자열만 받도록 프롬프트 강제.
 */
@Service
public class GptService {

    private final WebClient webClient;
    private final String model;
    private final ObjectMapper om = new ObjectMapper();
    private final SurveyRepository surveyRepository;

    public GptService(
            SurveyRepository surveyRepository,
            @Value("${openai.api.url:https://api.openai.com/v1}") String apiBaseUrl,
            @Value("${openai.api.key:}") String apiKey,
            @Value("${openai.model:gpt-4o-mini}") String model
    ) {
        this.surveyRepository = surveyRepository;
        this.model = model;
        this.webClient = WebClient.builder()
                .baseUrl(apiBaseUrl)
                .defaultHeaders(h -> h.setBearerAuth(Objects.requireNonNullElse(apiKey, "")))
                .build();
    }

    public KeywordResponse generateKeywords(PreferenceRequest req) {
        String prompt = buildPrompt(req);
        try {
            String content = webClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("""
                        {
                          "model": "%s",
                          "messages": [
                            {"role":"system","content":"You are a helpful assistant that outputs ONLY valid JSON."},
                            {"role":"user","content": %s}
                          ],
                          "temperature": 0.3
                        }
                    """.formatted(model, jsonString(prompt)))
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(e -> Mono.empty())
                    .block();

            List<String> keywords = new ArrayList<>();
            if (content != null) {
                JsonNode root = om.readTree(content);
                JsonNode node = root.path("choices").path(0).path("message").path("content");
                if (node.isTextual()) {
                    String json = node.asText();
                    JsonNode arr = om.readTree(json);
                    if (arr.isArray()) {
                        for (JsonNode x : arr) if (x.isTextual()) keywords.add(x.asText());
                    }
                }
            }

            // 최신 설문에 키워드 저장(있다면)
            if (!keywords.isEmpty()) {
                SurveyEntity latest = surveyRepository.findTopByOrderByIdDesc();
                if (latest != null) {
                    latest.setKeywords(String.join(" ", keywords));
                    surveyRepository.save(latest);
                }
            }
            return new KeywordResponse(keywords, prompt);
        } catch (Exception e) {
            return new KeywordResponse(List.of(), prompt); // 폴백
        }
    }

    private static String buildPrompt(PreferenceRequest req) {
        String mood = req.getMood() == null ? "기본" : req.getMood().name();
        String foodStyle = req.getFoodStyle() == null ? "기본" : req.getFoodStyle().name();
        String diningStyle = req.getDiningStyle() == null ? "기본" : req.getDiningStyle().name();
        String timeSlot = req.getTimeSlot() == null ? "기본" : req.getTimeSlot().name();
        String weather = req.getWeather() == null ? "기본" : req.getWeather().name();
        String location = req.getLocation() == null ? "미상" : req.getLocation();

        return """
            사용자의 외식 선호를 기반으로 한국어 검색 키워드 3~5개만 뽑아 JSON 배열로만 출력하세요.
            각 키워드는 1~4글자 명사/형태로, 중복/유사어는 제외합니다.
            반드시 ["키워드1","키워드2"] 형식만 출력하세요. 추가 설명/마크다운 금지.

            조건:
            - 기분: %s
            - 음식 성향: %s
            - 식사 방식: %s
            - 시간대: %s
            - 날씨: %s
            - 위치: %s

            예시 출력: ["건강식","혼밥","한식","저녁","역곡"]
            """.formatted(mood, foodStyle, diningStyle, timeSlot, weather, location);
    }

    private static String jsonString(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
