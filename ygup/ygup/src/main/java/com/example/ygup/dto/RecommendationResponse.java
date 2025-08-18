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
        private double lat;
        private double lng;
        private double rating;

        public Place(String name, String address, double lat, double lng, double rating) {
            this.name = name;
            this.address = address;
            this.lat = lat;
            this.lng = lng;
            this.rating = rating;
        }

        public String getName() { return name; }
        public String getAddress() { return address; }
        public double getLat() { return lat; }
        public double getLng() { return lng; }
        public double getRating() { return rating; }
    }
}

