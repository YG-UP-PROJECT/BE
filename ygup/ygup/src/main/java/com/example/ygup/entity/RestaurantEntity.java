package com.example.ygup.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
@Entity
@Table(name = "restaurant",
        indexes = {
                @Index(name = "idx_restaurant_place_id", columnList = "placeId"),
                @Index(name = "idx_restaurant_name", columnList = "name")
        })
public class RestaurantEntity {

    @Id
    @Column(length = 36)
    private String id;                      // UUID string (for front: string id)

    @PrePersist
    public void prePersist() {
        if (this.id == null) this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 타임스탬프
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 카카오맵 연동용
    @Column(length = 32)
    private String placeId;                 // Kakao place id (optional)

    // 프론트 요구 필드
    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 300)
    private String address;

    @Column(length = 40)
    private String phone;

    @Column(length = 500)
    private String photoUrl;                // 대표 사진 1개

    // 거리: 사용자 위치 기준으로 계산해서 응답에만 넣음
    @Transient
    private Integer distance;               // meters

    // 좌표(원하면 거리 계산에 사용)
    private Double latitude;
    private Double longitude;

    // 리뷰
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReviewEntity> reviews = new ArrayList<>();
}
