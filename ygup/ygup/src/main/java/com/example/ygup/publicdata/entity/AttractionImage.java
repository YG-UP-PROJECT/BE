// src/main/java/com/example/ygup/publicdata/entity/AttractionImage.java
package com.example.ygup.publicdata.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "attraction_images")
public class AttractionImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @Column(length = 500)
    private String originUrl;

    @Column(length = 500)
    private String smallUrl;

    public AttractionImage() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getContentId() {
        return contentId;
    }
    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }
    public String getOriginUrl() {
        return originUrl;
    }
    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }
    public String getSmallUrl() {
        return smallUrl;
    }
    public void setSmallUrl(String smallUrl) {
        this.smallUrl = smallUrl;
    }
}
