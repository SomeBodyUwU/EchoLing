package com.example.echoling.collection;

import java.util.List;

public class CollectionDTO {

    private String name;

    private List<Long> translationIds;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getTranslationIds() {
        return translationIds;
    }

    public void setTranslationIds(List<Long> translationIds) {
        this.translationIds = translationIds;
    }
}