package com.example.ygup.dto;

import java.util.List;

public class RecommendationResponse {
    private String location;
    private List<Place> places;

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
        private String name;
        private String address;
        private Double lat;
        private Double lng;
        private Double rating;

        public Place(String name, String address, Double lat, Double lng, Double rating) {
            this.name = name;
            this.address = address;
            this.lat = lat;
            this.lng = lng;
            this.rating = rating;
        }

        public String getName() { return name; }
        public String getAddress() { return address; }
        public Double getLat() { return lat; }
        public Double getLng() { return lng; }
        public Double getRating() { return rating; }
    }
}

