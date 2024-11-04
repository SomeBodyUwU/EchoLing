package com.example.echoling.data.Translation;

import jakarta.persistence.*;

@Entity
@Table(name = "translation_entries")
public class TranslationEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sourceText;
    private String translatedText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "translation_id")
    private Translation translation;

    public TranslationEntry(String sourceText, String translatedText, Translation translation) {
        this.sourceText = sourceText;
        this.translatedText = translatedText;
        this.translation = translation;
    }

    public TranslationEntry() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceText() {
        return processText(sourceText);
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = processText(translatedText);
    }

    public Translation getTranslation() {
        return translation;
    }

    public void setTranslation(Translation translation) {
        this.translation = translation;
    }
    private String processText(String text){
        return text.replace("<", "&lt;").replace(">","&gt;");
    }
}