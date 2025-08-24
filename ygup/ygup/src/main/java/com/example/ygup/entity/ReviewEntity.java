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
@Table(name = "review")
public class ReviewEntity {

    @Id
    @Column(length = 36)
    private String id;

    @PrePersist
    public void prePersist() {
        if (this.id == null) this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }

    // 식당 연결
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id")
    private RestaurantEntity restaurant;

    // 프론트 요구 필드
    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private Integer rating;

    // 선택 키워드
    @ElementCollection
    @CollectionTable(name = "review_keywords", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "keyword", length = 50)
    @Builder.Default
    private List<String> keywords = new ArrayList<>();

    // 작성 시간
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}