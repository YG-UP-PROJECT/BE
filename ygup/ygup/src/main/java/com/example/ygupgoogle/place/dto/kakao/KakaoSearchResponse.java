package com.example.ygupgoogle.place.dto.kakao;

import java.util.List;

public record KakaoSearchResponse(
        List<Document> documents
) {
    public record Document(
            String place_name,
            String category_name,
            String address_name,
            String road_address_name,
            String x,   // 경도 문자열
            String y,   // 위도 문자열
            String place_url
    ) {}
}
