// src/main/java/com/example/ygup/service/GptService.java
package com.example.ygup.service;

import com.example.ygup.dto.PreferenceRequest;
import com.example.ygup.dto.KeywordResponse;
import com.example.ygup.entity.SurveyEntity;
import com.example.ygup.survey.SurveyRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class GptService {

    private final WebClient webClient;
    private final SurveyRepository surveyRepository;
    private final String geminiApiKey;
    private final ObjectMapper om = new ObjectMapper();

    @Autowired
    public GptService(WebClient.Builder webClientBuilder,
                      SurveyRepository surveyRepository,
                      @Value("${gemini.api-key}") String geminiApiKey) {
        this.webClient = webClientBuilder.baseUrl("https://generativelanguage.googleapis.com").build();
        this.surveyRepository = surveyRepository;
        this.geminiApiKey = geminiApiKey;
    }

    public KeywordResponse generateKeywords(PreferenceRequest request) {
        String prompt = generatePromptFromRequest(request);

        String body = """
        {
          "contents":[
            {
              "role":"user",
              "parts":[{"text": %s}]
            }
          ],
          "generationConfig": {
            "temperature": 0.6
          }
        }
        """.formatted(jsonString(prompt));

        String response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/models/gemini-1.5-flash:generateContent")
                        .queryParam("key", geminiApiKey)
                        .build())
                .header("Content-Type", "application/json")
                .bodyValue(body)
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
        try {
            JsonNode root = om.readTree(response);
            // 표준 경로: candidates[0].content.parts[*].text
            StringBuilder sb = new StringBuilder();
            var cands = root.path("candidates");
            if (cands.isArray() && cands.size() > 0) {
                var parts = cands.get(0).path("content").path("parts");
                if (parts.isArray()) {
                    for (JsonNode p : parts) {
                        String t = p.path("text").asText("");
                        if (!t.isBlank()) sb.append(t).append("\n");
                    }
                }
            }
            String text = sb.toString().trim();
            if (text.isBlank()) return List.of();

            // 쉼표/개행/대괄호/따옴표 제거 기준으로 키워드 뽑기
            text = text.replaceAll("[\\[\\]\\-•\\n\\r]", ",");
            String[] tokens = text.split(",");
            List<String> out = new ArrayList<>();
            for (String tk : tokens) {
                String s = tk.trim();
                if (s.isEmpty()) continue;
                // "키워드:" 형태 제거
                s = s.replaceAll("(?i)keyword(s)?\\s*:","").trim();
                if (!s.isEmpty()) out.add(s);
            }
            // 3~8개로 제한 (과다 생성 방지)
            if (out.size() > 8) return out.subList(0, 8);
            return out;
        } catch (Exception e) {
            // 실패 시 최소 안전 기본값
            return List.of("분위기 좋은 카페", "건강한 집밥", "비 오는 날 감성 식당");
        }
    }

    private String generatePromptFromRequest(PreferenceRequest req) {
        // NPE 방지 가드
        String mood = req.getMood() == null ? "기분 보통" : req.getMood().name();
        String weatherStr = req.getWeather() == null ? "날씨 정보 없음" : req.getWeather().name();

        return """
        아래 설문 결과와 날씨/시간 정보를 보고 한국어 '맛집/장소' 검색에 바로 쓸 수 있는 **키워드 구절**을 5~7개 추천해줘.
        - 톤: 간결, 실사용 검색어
        - 출력: 쉼표로 구분된 키워드 나열만 (설명 금지)

        [설문]
        - 분위기: %s
        - 음식 스타일: %s
        - 식사 스타일: %s
        - 시간대: %s
        - 날씨: %s
        - 위치 힌트: %s
        """.formatted(
                mood,
                req.getFoodStyle() == null ? "기본" : req.getFoodStyle().name(),
                req.getDiningStyle() == null ? "기본" : req.getDiningStyle().name(),
                req.getTimeSlot() == null ? "기본" : req.getTimeSlot().name(),
                weatherStr,
                req.getLocation() == null ? "미상" : req.getLocation()
        );
    }

    private static String jsonString(String s) {
        // JSON 안전 문자열로 변환
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
