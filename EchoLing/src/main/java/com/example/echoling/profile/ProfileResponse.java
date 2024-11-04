package com.example.echoling.profile;

import com.example.echoling.statics.Language;

import java.util.List;

public class ProfileResponse {
    private String username;
    private String email;
    private String name;
    private String surname;
    private String role;
    private List<String> selectedNativeLanguages;
    private List<String> selectedLearningLanguages;

    public ProfileResponse(String username, String email, String name, String surname, String role,
                           List<String> selectedNativeLanguages, List<String> selectedLearningLanguages) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.selectedNativeLanguages = selectedNativeLanguages;
        this.selectedLearningLanguages = selectedLearningLanguages;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getSelectedNativeLanguages() {
        return selectedNativeLanguages;
    }

    public void setSelectedNativeLanguages(List<String> selectedNativeLanguages) {
        this.selectedNativeLanguages = selectedNativeLanguages;
    }

    public List<String> getSelectedLearningLanguages() {
        return selectedLearningLanguages;
    }

    public void setSelectedLearningLanguages(List<String> selectedLearningLanguages) {
        this.selectedLearningLanguages = selectedLearningLanguages;
    }
}