package com.example.ygup.dto;

import java.util.List;

public class KeywordResponse {
    private List<String> keywords;
    private String prompt;

    public KeywordResponse() {}

    public KeywordResponse(List<String> keywords, String prompt) {
        this.keywords = keywords;
        this.prompt = prompt;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}