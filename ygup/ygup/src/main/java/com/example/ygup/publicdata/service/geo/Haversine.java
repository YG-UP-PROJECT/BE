// src/main/java/com/example/ygup/publicdata/service/geo/Haversine.java
package com.example.ygup.publicdata.service.geo;

public class Haversine {
    private static final double EARTH_RADIUS_M = 6371000.0;

    public static long distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return Math.round(EARTH_RADIUS_M * c);
    }
}
