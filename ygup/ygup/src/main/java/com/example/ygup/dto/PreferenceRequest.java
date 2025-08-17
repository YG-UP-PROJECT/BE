package com.example.ygup.dto;

import com.example.ygup.enums.*;

public class PreferenceRequest {
    private Mood mood;
    private FoodStyle foodStyle;
    private DiningStyle diningStyle;
    private TimeSlot timeSlot;
    private Weather weather;
    private TempBand tempBand;

    // 위치 정보 추가
    private Double latitude;   // 위도
    private Double longitude;  // 경도
    private String location;   // 동네 이름 (ex: "부천시 역곡동")

    public PreferenceRequest() {}

    public Mood getMood() { return mood; }
    public void setMood(Mood mood) { this.mood = mood; }

    public FoodStyle getFoodStyle() { return foodStyle; }
    public void setFoodStyle(FoodStyle foodStyle) { this.foodStyle = foodStyle; }

    public DiningStyle getDiningStyle() { return diningStyle; }
    public void setDiningStyle(DiningStyle diningStyle) { this.diningStyle = diningStyle; }

    public TimeSlot getTimeSlot() { return timeSlot; }
    public void setTimeSlot(TimeSlot timeSlot) { this.timeSlot = timeSlot; }

    public Weather getWeather() { return weather; }
    public void setWeather(Weather weather) { this.weather = weather; }

    public TempBand getTempBand() { return tempBand; }
    public void setTempBand(TempBand tempBand) { this.tempBand = tempBand; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
