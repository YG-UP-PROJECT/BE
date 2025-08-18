package com.example.ygup.entity;

import com.example.ygup.enums.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "surveys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Mood mood;

    @Enumerated(EnumType.STRING)
    private FoodStyle foodStyle;

    @Enumerated(EnumType.STRING)
    private DiningStyle diningStyle;

    @Enumerated(EnumType.STRING)
    private TimeSlot timeSlot;

    @Column(name = "weather")
    private String weather;

    private Integer tempC;

    private String comboCode;

    @Column(columnDefinition = "TEXT")
    private String keywords;
}
