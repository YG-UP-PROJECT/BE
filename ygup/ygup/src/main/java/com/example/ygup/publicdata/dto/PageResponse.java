// src/main/java/com/example/ygup/publicdata/dto/PageResponse.java
package com.example.ygup.publicdata.dto;

import java.util.List;

public class PageResponse<T> {

    private int page;
    private int size;
    private long total;
    private List<T> items;

    public PageResponse() {}

    public PageResponse(int page, int size, long total, List<T> items) {
        this.page = page;
        this.size = size;
        this.total = total;
        this.items = items;
    }

    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public long getTotal() {
        return total;
    }
    public void setTotal(long total) {
        this.total = total;
    }
    public List<T> getItems() {
        return items;
    }
    public void setItems(List<T> items) {
        this.items = items;
    }
}
