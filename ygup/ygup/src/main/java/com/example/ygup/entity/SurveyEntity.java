package com.example.ygup.entity;

import com.example.ygup.survey.dto.SurveyCreateRequest.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
@Entity
@Table(name = "survey")
public class SurveyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // MySQL AUTO_INCREMENT → 첫 레코드가 1
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 설문 4가지
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16)
    private Mood mood;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16)
    private FoodStyle foodStyle;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16)
    private DiningStyle diningStyle;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16)
    private TimeSlot timeSlot;

    // 날씨 스냅샷
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16)
    private Weather weather;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16)
    private TempBand tempBand;

    private Integer tempC;

    // 위치

    private Double latitude;


    private Double longitude;

    // 파생 코드
    @Column(nullable = false, length = 8)
    private String comboCode;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
