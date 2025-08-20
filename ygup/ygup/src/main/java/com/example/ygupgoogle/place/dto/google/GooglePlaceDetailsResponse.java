package com.example.ygupgoogle.place.dto.google;

import java.util.List;

public record GooglePlaceDetailsResponse(
        Result result
) {
    public record Result(
            Double rating,
            Integer user_ratings_total,
            List<Review> reviews
    ) {
        public record Review(
                String author_name,
                Integer rating,
                String text
        ) {}
    }
}
