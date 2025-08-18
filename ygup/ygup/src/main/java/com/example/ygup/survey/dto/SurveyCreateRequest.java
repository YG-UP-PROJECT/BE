package com.example.ygup.survey.dto;

import com.example.ygup.enums.Mood;
import com.example.ygup.enums.FoodStyle;
import com.example.ygup.enums.DiningStyle;
import com.example.ygup.enums.TimeSlot;
import jakarta.validation.constraints.NotNull;

public record SurveyCreateRequest(
        @NotNull Mood mood,
        @NotNull FoodStyle foodStyle,
        @NotNull DiningStyle diningStyle,
        @NotNull TimeSlot timeSlot,
        String weather,
        Integer tempC
) {}
