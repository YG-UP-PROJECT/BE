// src/main/java/com/example/ygup/publicdata/dto/AttractionSummaryDto.java
package com.example.ygup.publicdata.dto;

public class AttractionSummaryDto {

    private Long contentId;
    private String title;
    private String addr;
    private Double mapX; // 경도(lng)
    private Double mapY; // 위도(lat)
    private String thumbnail;
    private String category;
    private Long distanceMeters; // nearby 결과용(선택)

    public AttractionSummaryDto() {}

    public Long getContentId() {
        return contentId;
    }
    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAddr() {
        return addr;
    }
    public void setAddr(String addr) {
        this.addr = addr;
    }
    public Double getMapX() {
        return mapX;
    }
    public void setMapX(Double mapX) {
        this.mapX = mapX;
    }
    public Double getMapY() {
        return mapY;
    }
    public void setMapY(Double mapY) {
        this.mapY = mapY;
    }
    public String getThumbnail() {
        return thumbnail;
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public Long getDistanceMeters() {
        return distanceMeters;
    }
    public void setDistanceMeters(Long distanceMeters) {
        this.distanceMeters = distanceMeters;
    }
}
