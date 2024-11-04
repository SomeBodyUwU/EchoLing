package com.example.echoling.translation;

public class TranslationEntryRequest {
    private String sourceText;
    private String translatedText;

    public TranslationEntryRequest() {
    }

    public TranslationEntryRequest(String sourceText, String translatedText) {
        this.sourceText = sourceText;
        this.translatedText = translatedText;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }
}
