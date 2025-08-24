package com.example.ygup.survey.dto;

import com.example.ygup.enums.*;

public record SurveyCreateResponse(
        Long surveyId,
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