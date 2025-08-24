// src/main/java/com/example/ygup/entity/PlaceMappingEntity.java
package com.example.ygup.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "place_mapping")
public class PlaceMappingEntity {

    @Id
    @Column(length = 32) // Kakao place id 길이 여유
    private String kakaoId;

    @Column(length = 64)
    private String googlePlaceId;

    // 편의 정보 (검색/디버깅용)
    @Column(length = 200) private String name;
    @Column(length = 300) private String address;
    private Double lat; // 위도
    private Double lng; // 경도
}