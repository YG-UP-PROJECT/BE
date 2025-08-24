package com.example.ygup.dto;

import java.util.List;

public class RecommendationResponse {
    private final String location;
    private final List<Place> places;

    public RecommendationResponse(String location, List<Place> places) {
        this.location = location;
        this.places = places;
    }

    public String getLocation() {
        return location;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public static class Place {
        private final String kakaoId;        // 카카오 고유 id
        private final String name;
        private final String address;
        private final Double lat;
        private final Double lng;
        private final Double rating;         // 카카오는 null 유지
        private final String kakaoPlaceUrl;  // 카카오 상세 페이지 URL

        public Place(String kakaoId, String name, String address,
                     Double lat, Double lng, Double rating, String kakaoPlaceUrl) {
            this.kakaoId = kakaoId;
            this.name = name;
            this.address = address;
            this.lat = lat;
            this.lng = lng;
            this.rating = rating;
            this.kakaoPlaceUrl = kakaoPlaceUrl;
        }
        public Place(String name, String address, Double lat, Double lng, Double rating) {
            this(null, name, address, lat, lng, rating, null);
        }

        public String getKakaoId()       { return kakaoId; }
        public String getName()          { return name; }
        public String getAddress()       { return address; }
        public Double getLat()           { return lat; }
        public Double getLng()           { return lng; }
        public Double getRating()        { return rating; }
        public String getKakaoPlaceUrl() { return kakaoPlaceUrl; } // ← 메서드명 주의
    }
}
