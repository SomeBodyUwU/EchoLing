package com.example.echoling.statics;

public class Language {
    private String name;
    private String flagUrl;

    public Language(String name, String flagUrl) {
        this.name = name;
        this.flagUrl = flagUrl;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlagUrl() {
        return flagUrl;
    }

    public void setFlagUrl(String flagUrl) {
        this.flagUrl = flagUrl;
    }
}