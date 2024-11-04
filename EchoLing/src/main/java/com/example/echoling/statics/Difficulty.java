package com.example.echoling.statics;

public class Difficulty {
    private String level;
    private String imageUrl;

    public Difficulty(String level, String imageUrl) {
        this.level = level;
        this.imageUrl = imageUrl;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}