package com.example.ygup.dto;

import java.util.List;

public class KeywordResponse {
    private List<String> keywords;
    private String prompt; // 디버깅/확인용(원하면 지워도 됨)

    public KeywordResponse() {}

    public KeywordResponse(List<String> keywords, String prompt) {
        this.keywords = keywords;
        this.prompt = prompt;
    }

    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
}

