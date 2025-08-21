package com.example.ygup.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@Table(
        name = "restaurant",
        indexes = {
                @Index(name = "ix_restaurant_name", columnList = "name"),
                @Index(name = "ix_restaurant_lat_lng", columnList = "lat,lng")
        }
)
public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 가게명 */
    @Column(nullable = false, length = 150)
    private String name;

    /** 지번/도로명 주소 (둘 중 하나 채움) */
    @Column(length = 300)
    private String address;

    @Column(length = 300)
    private String roadAddress;

    /** 좌표 (Kakao: x=lng, y=lat) */
    private Double lat;   // 위도
    private Double lng;   // 경도

    /** Kakao 상세 페이지 URL (선택) */
    @Column(length = 400)
    private String kakaoPlaceUrl;

    /** 생성/수정 시각 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /** 리뷰 양방향 매핑 (필요 시) */
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReviewEntity> reviews = new ArrayList<>();

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
