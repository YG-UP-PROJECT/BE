// src/main/java/com/example/ygup/dto/PlaceCard.java
package com.example.ygup.dto;

import java.util.List;

public record PlaceCard(
        String kakaoId,
        String name,
        String address,
        Double lat,
        Double lng,
        Double rating,         // 구글 평점
        String kakaoPlaceUrl,  // 카카오 딥링크
        List<String> photoUrls // ★ 사진 프록시 URL 리스트 (/places/photo?... 형태)
) {}
