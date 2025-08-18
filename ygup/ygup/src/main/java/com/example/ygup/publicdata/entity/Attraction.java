// src/main/java/com/example/ygup/publicdata/entity/Attraction.java
package com.example.ygup.publicdata.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attractions")
public class Attraction {

    @Id
    @Column(name = "content_id")
    private Long contentId; // TourAPI contentId를 PK로 사용

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 300)
    private String addr1;

    @Column(length = 300)
    private String addr2;

    @Column(length = 50)
    private String tel;

    private Double mapX; // 경도
    private Double mapY; // 위도

    @Column(length = 500)
    private String firstImage;

    @Column(length = 20)
    private String cat1;
    @Column(length = 20)
    private String cat2;
    @Column(length = 20)
    private String cat3;

    private Integer areaCode;
    private Integer sigunguCode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Attraction() {}

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

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
    public String getAddr1() {
        return addr1;
    }
    public void setAddr1(String addr1) {
        this.addr1 = addr1;
    }
    public String getAddr2() {
        return addr2;
    }
    public void setAddr2(String addr2) {
        this.addr2 = addr2;
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
    public String getFirstImage() {
        return firstImage;
    }
    public void setFirstImage(String firstImage) {
        this.firstImage = firstImage;
    }
    public String getCat1() {
        return cat1;
    }
    public void setCat1(String cat1) {
        this.cat1 = cat1;
    }
    public String getCat2() {
        return cat2;
    }
    public void setCat2(String cat2) {
        this.cat2 = cat2;
    }
    public String getCat3() {
        return cat3;
    }
    public void setCat3(String cat3) {
        this.cat3 = cat3;
    }
    public Integer getAreaCode() {
        return areaCode;
    }
    public void setAreaCode(Integer areaCode) {
        this.areaCode = areaCode;
    }
    public Integer getSigunguCode() {
        return sigunguCode;
    }
    public void setSigunguCode(Integer sigunguCode) {
        this.sigunguCode = sigunguCode;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
