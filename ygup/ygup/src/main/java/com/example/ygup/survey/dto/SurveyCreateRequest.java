package com.example.ygup.survey.dto;

import jakarta.validation.constraints.*;

public record SurveyCreateRequest(
        @NotNull Mood mood,               // QUIET | NOISY
        @NotNull FoodStyle foodStyle,     // HEALTHY | EXCITING
        @NotNull DiningStyle diningStyle, // ALONE | TOGETHER
        @NotNull TimeSlot timeSlot,       // LUNCH | DINNER

        @NotNull Weather weather,         // CLEAR | CLOUD | RAIN | SNOW
        @NotNull TempBand tempBand,       // HOT | MILD | COOL | COLD | VERY_COLD

        Integer tempC,                    // 선택
        @NotNull Location location        // 위경도
) {
    public record Location(
            @NotNull @DecimalMin("-90.0")  @DecimalMax("90.0") Double latitude,
            @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude
    ) {}

    public enum Mood { QUIET, NOISY }
    public enum FoodStyle { HEALTHY, EXCITING }
    public enum DiningStyle { ALONE, TOGETHER }
    public enum TimeSlot { LUNCH, DINNER }
    public enum Weather { CLEAR, CLOUD, RAIN, SNOW }
    public enum TempBand { HOT, MILD, COOL, COLD, VERY_COLD }
}
