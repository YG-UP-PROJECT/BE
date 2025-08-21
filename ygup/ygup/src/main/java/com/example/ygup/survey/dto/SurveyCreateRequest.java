package com.example.ygup.survey.dto;

import jakarta.validation.constraints.*;
import com.example.ygup.enums.*;

public record SurveyCreateRequest(
        @NotNull Mood mood,
        @NotNull FoodStyle foodStyle,
        @NotNull DiningStyle diningStyle,
        @NotNull TimeSlot timeSlot,
        @NotNull Weather weather,

        Integer tempC,                    // 선택
        @NotNull Location location        // 위경도 (받지만 서비스에서 역곡으로 고정 적용)
) {
    public record Location(
            @NotNull @DecimalMin("-90.0")  @DecimalMax("90.0") Double latitude,
            @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude
    ) {}
}
