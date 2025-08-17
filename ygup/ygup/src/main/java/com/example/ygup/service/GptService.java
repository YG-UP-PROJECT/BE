package com.example.ygup.service;

import com.example.ygup.dto.KeywordResponse;
import com.example.ygup.dto.PreferenceRequest;
import com.example.ygup.enums.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class GptService {
    // 이름은 그대로 두되, 내부는 Google Gemini 호출로 변경

    private final ObjectMapper om = new ObjectMapper();

    @Value("${gemini.api-key:}")
    private String apiKey;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String model;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com")
            .build();

    public KeywordResponse generateKeywords(PreferenceRequest req) {
        String prompt = buildPrompt(req);

        // 🔸 API 키가 없으면(로컬/오프라인) 규칙기반 Fallback
        if (apiKey == null || apiKey.isBlank()) {
            return new KeywordResponse(fallbackKeywords(req), prompt);
        }

        /*
         * Google Gemini (Generative Language API) 호출 규격
         * POST https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key=API_KEY
         * Body:
         * {
         *   "contents": [{"parts": [{"text": "<prompt>"}]}],
         *   "generationConfig": {"temperature": 0.2}
         * }
         */
        String body = """
        {
          "contents": [
            { "parts": [ { "text": %s } ] }
          ],
          "generationConfig": { "temperature": 0.2 }
        }
        """.formatted(om.valueToTree(prompt).toString());

        try {
            String url = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s", model, apiKey);

            String result = webClient.post()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // 응답 파싱: candidates[0].content.parts[0].text
            JsonNode root = om.readTree(result);
            JsonNode firstCandidate = root.path("candidates").get(0);
            String content = firstCandidate.path("content").path("parts").get(0).path("text").asText();

            // 모델에 "JSON만" 내놓으라 했으니 그대로 파싱
            JsonNode json = om.readTree(content);
            List<String> kws = new ArrayList<>();
            for (JsonNode n : json.path("keywords")) {
                kws.add(n.asText());
            }

            if (kws.isEmpty()) {
                kws = fallbackKeywords(req);
            }

            return new KeywordResponse(kws, prompt);
        } catch (Exception e) {
            // 실패 시 Fallback
            return new KeywordResponse(fallbackKeywords(req), prompt);
        }
    }

    // 🔹 위치/선호 정보를 반영한 프롬프트
    private String buildPrompt(PreferenceRequest r) {
        String baseLocation = (r.getLocation() != null && !r.getLocation().isBlank())
                ? r.getLocation()
                : "역곡"; // 기본값

        String coordInfo = (r.getLatitude() != null && r.getLongitude() != null)
                ? "(lat: " + r.getLatitude() + ", lon: " + r.getLongitude() + ")"
                : "";

        // Gemini가 JSON만 출력하게 강하게 요구
        return """
        You are a restaurant keyword generator for Kakao Map. 
        Return STRICT JSON only. No extra text.

        Task:
        - Generate 3-5 concise Korean search keywords for Kakao Map near "%s" %s.
        - Use short noun phrases only (no sentences, no explanations).
        - Tailor to the user's preferences below.

        Inputs:
        - mood: %s
        - foodStyle: %s
        - diningStyle: %s
        - timeSlot: %s
        - weather: %s
        - tempBand: %s
        - location: %s %s

        Output JSON schema:
        {
          "keywords": ["역곡 이자카야","역곡 샐러드","역곡 조용한 카페"]
        }
        """.formatted(
                baseLocation, coordInfo,
                r.getMood(), r.getFoodStyle(), r.getDiningStyle(),
                r.getTimeSlot(), r.getWeather(), r.getTempBand(),
                baseLocation, coordInfo
        );
    }

    // 아주 단순한 규칙기반 키워드(오프라인/에러시)
    private List<String> fallbackKeywords(PreferenceRequest r) {
        List<String> out = new ArrayList<>();
        if (r != null) {
            FoodStyle fs = r.getFoodStyle();
            Mood m = r.getMood();
            DiningStyle ds = r.getDiningStyle();

            if (fs == FoodStyle.HEALTHY) out.add("역곡 샐러드");
            if (fs == FoodStyle.EXCITING) out.add("역곡 이자카야");
            if (m == Mood.QUIET) out.add("역곡 조용한 카페");
            if (ds == DiningStyle.ALONE) out.add("역곡 혼밥");
        }
        if (out.isEmpty()) out.add("역곡 맛집");
        return out;
    }
}
