// 응답에 묶어서 보낼 최종 요약
package com.example.ygupgoogle.place.dto;

import java.util.List;

public record PlaceSummary(
        String kakaoId,
        String googlePlaceId,// ✅ 추가
        String name,
        String category,
        String address,
        String roadAddress,
        double kakaoX,     // 경도
        double kakaoY,     // 위도
        String kakaoPlaceUrl,
        Double googleRating,           // null 가능
        Integer googleUserRatingsTotal,// null 가능
        List<GoogleReview> googleReviews, // 0~3개
        List<String> photoUrls,
        List<String> photoAttributions
) {
    public record GoogleReview(String author, String text, Integer rating){}
}
