package com.example.ygup.dto;

import com.example.ygup.enums.*;

public class PreferenceRequest {
    private Mood mood;
    private FoodStyle foodStyle;
    private DiningStyle diningStyle;
    private TimeSlot timeSlot;

    private Weather weather;   // enum으로 통일

    private Double latitude;
    private Double longitude;
    private String location;
    private String prompt;

    public PreferenceRequest() {}

    public Mood getMood() { return mood; }
    public void setMood(Mood mood) { this.mood = mood; }

    public FoodStyle getFoodStyle() { return foodStyle; }
    public void setFoodStyle(FoodStyle foodStyle) { this.foodStyle = foodStyle; }

    public DiningStyle getDiningStyle() { return diningStyle; }
    public void setDiningStyle(DiningStyle diningStyle) { this.diningStyle = diningStyle; }

    public TimeSlot getTimeSlot() { return timeSlot; }
    public void setTimeSlot(TimeSlot timeSlot) { this.timeSlot = timeSlot; }

    public Weather getWeather() { return weather; }              // ✅ enum 반환
    public void setWeather(Weather weather) { this.weather = weather; } // ✅ enum 받음

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
}
