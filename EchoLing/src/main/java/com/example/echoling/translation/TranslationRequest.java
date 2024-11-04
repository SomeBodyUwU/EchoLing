package com.example.echoling.translation;

public class TranslationRequest {
    private String name;
    private String description;
    private String difficulty;
    private String sourceLang;
    private String targetLang;
    private boolean isPrivate;

    public TranslationRequest() {
    }

    public TranslationRequest(String name, String description, String difficulty, String sourceLang, String targetLang, boolean isPrivate) {
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
        this.isPrivate = isPrivate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getSourceLang() {
        return sourceLang;
    }

    public void setSourceLang(String sourceLang) {
        this.sourceLang = sourceLang;
    }

    public String getTargetLang() {
        return targetLang;
    }

    public void setTargetLang(String targetLang) {
        this.targetLang = targetLang;
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
}
