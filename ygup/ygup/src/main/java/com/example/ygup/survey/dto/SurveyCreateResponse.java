package com.example.ygup.survey.dto;

import com.example.ygup.domain.SurveyEnums.*;

public record SurveyCreateResponse(
        Long surveyId,               // 저장된 PK 반환
        String comboCode,
        SurveySnapshot snapshot
) {
    public record SurveySnapshot(
            Mood mood,
            FoodStyle foodStyle,
            DiningStyle diningStyle,
            TimeSlot timeSlot,
            Weather weather,
            Integer tempC,
            String locationName
    ) {}
}
