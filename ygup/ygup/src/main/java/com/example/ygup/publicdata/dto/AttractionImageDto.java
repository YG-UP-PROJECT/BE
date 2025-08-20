// src/main/java/com/example/ygup/publicdata/dto/AttractionImageDto.java
package com.example.ygup.publicdata.dto;

public class AttractionImageDto {
    private String originUrl;
    private String smallUrl;

    public AttractionImageDto() {}

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
