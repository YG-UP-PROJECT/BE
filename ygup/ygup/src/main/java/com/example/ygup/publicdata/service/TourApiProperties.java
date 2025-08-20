// src/main/java/com/example/ygup/publicdata/service/TourApiProperties.java
package com.example.ygup.publicdata.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "publicdata.tourapi")
public class TourApiProperties {

    private String baseUrl;
    private String serviceKey;
    private int timeoutMs = 3000;
    private int pageSizeDefault = 20;

    public TourApiProperties() {}

    public String getBaseUrl() {
        return baseUrl;
    }
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    public String getServiceKey() {
        return serviceKey;
    }
    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }
    public int getTimeoutMs() {
        return timeoutMs;
    }
    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }
    public int getPageSizeDefault() {
        return pageSizeDefault;
    }
    public void setPageSizeDefault(int pageSizeDefault) {
        this.pageSizeDefault = pageSizeDefault;
    }
}
