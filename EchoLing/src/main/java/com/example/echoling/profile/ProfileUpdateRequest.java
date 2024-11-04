package com.example.echoling.profile;

import java.util.List;

public class ProfileUpdateRequest {
    private String name;
    private String surname;
    private List<String> nativeLanguages;
    private List<String> learningLanguages;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public List<String> getNativeLanguages() {
        return nativeLanguages;
    }

    public void setNativeLanguages(List<String> nativeLanguages) {
        this.nativeLanguages = nativeLanguages;
    }

    public List<String> getLearningLanguages() {
        return learningLanguages;
    }

    public void setLearningLanguages(List<String> learningLanguages) {
        this.learningLanguages = learningLanguages;
    }
}