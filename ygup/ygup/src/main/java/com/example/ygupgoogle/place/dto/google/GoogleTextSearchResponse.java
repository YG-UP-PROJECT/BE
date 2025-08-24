package com.example.ygupgoogle.place.dto.google;

import java.util.List;

public record GoogleTextSearchResponse(
        List<Result> results
) {
    public record Result(
            String name,
            String place_id,
            Geometry geometry,
            List<Photo> photos
    ) {
        public record Geometry(Location location) {
            public record Location(double lat, double lng) {}
        }
        public record Photo(
                String photo_reference,
                List<String> html_attributions,
                Integer width,
                Integer height
        ){}
    }
}