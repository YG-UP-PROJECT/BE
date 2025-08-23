package com.example.ygupgoogle.place.dto;

import java.util.List;

public record PlaceDetailsResponse(
        String kakaoId,
        String googlePlaceId,// 선택(있으면 프론트 딥링크용으로 같이 반환)
        String name,
        String address,                 // Google formatted_address
        Double googleRating,
        Integer googleUserRatingsTotal,
        List<Review> googleReviews,     // 상위 3개
        List<String> photoUrls,         // /places/photo?ref=... 프록시 URL
        List<String> photoAttributions
) {
    public record Review(String author, Integer rating, String text) {}
}
