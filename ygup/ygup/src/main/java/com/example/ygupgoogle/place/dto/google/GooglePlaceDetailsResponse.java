package com.example.ygupgoogle.place.dto.google;

import java.util.List;

public record GooglePlaceDetailsResponse(
        Result result
) {
    public record Result(
            String name,
            String formatted_address,
            Double rating,
            Integer user_ratings_total,
            List<Review> reviews,
            List<Photo> photos
    ) {
        public record Review(
                String author_name,
                Integer rating,
                String text
        ) {}
        public record Photo( // ▼ 반드시 추가
                             String photo_reference,
                             List<String> html_attributions,
                             Integer width,
                             Integer height
        ) {}
    }
}