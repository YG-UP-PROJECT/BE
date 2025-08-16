package com.example.ygup.survey.dto;

public record SurveyCreateResponse(
        Long surveyId,               // ★ 저장된 PK 반환
        String comboCode,
        SurveySnapshot snapshot
) {
    public record SurveySnapshot(
            SurveyCreateRequest.Mood mood,
            SurveyCreateRequest.FoodStyle foodStyle,
            SurveyCreateRequest.DiningStyle diningStyle,
            SurveyCreateRequest.TimeSlot timeSlot,
            SurveyCreateRequest.Weather weather,
            SurveyCreateRequest.TempBand tempBand,
            Integer tempC,
            double latitude,
            double longitude
    ) {}
}
