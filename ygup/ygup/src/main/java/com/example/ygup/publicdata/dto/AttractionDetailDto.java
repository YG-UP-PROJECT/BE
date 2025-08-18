// src/main/java/com/example/ygup/publicdata/dto/AttractionDetailDto.java
package com.example.ygup.publicdata.dto;

import java.util.ArrayList;
import java.util.List;

public class AttractionDetailDto {

    private Long contentId;
    private String title;
    private String addr;
    private String tel;
    private Double mapX;
    private Double mapY;
    private String overview;     // 소개/설명
    private String firstImage;   // 대표 이미지
    private List<AttractionImageDto> images = new ArrayList<>();

    public AttractionDetailDto() {}

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
    public String getTel() {
        return tel;
    }
    public void setTel(String tel) {
        this.tel = tel;
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
    public String getOverview() {
        return overview;
    }
    public void setOverview(String overview) {
        this.overview = overview;
    }
    public String getFirstImage() {
        return firstImage;
    }
    public void setFirstImage(String firstImage) {
        this.firstImage = firstImage;
    }
    public List<AttractionImageDto> getImages() {
        return images;
    }
    public void setImages(List<AttractionImageDto> images) {
        this.images = images;
    }
}
