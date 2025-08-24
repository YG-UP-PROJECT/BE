// src/main/java/com/example/ygup/dto/IncomingSurveyRequest.java
package com.example.ygup.dto;

import com.example.ygup.enums.*;
import jakarta.validation.constraints.NotNull;

public record IncomingSurveyRequest(
        @NotNull Mood mood,
        @NotNull FoodStyle foodStyle,
        @NotNull DiningStyle diningStyle,
        @NotNull TimeSlot timeSlot,
        @NotNull Weather weather,
        String location // null/빈값이면 서버에서 기본값 사용
) {}
