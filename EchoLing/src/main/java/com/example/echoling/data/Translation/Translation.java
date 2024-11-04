package com.example.echoling.data.Translation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.example.echoling.data.Collection.Collection;
import com.example.echoling.data.User.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@NamedQueries({
    @NamedQuery(
        name = "Translation.searchByKeyword",
        query = "SELECT t FROM Translation t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))"
    )
})
@Table(name = "translations")
public class Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String difficulty;
    private String sourceLang;
    private String targetLang;
    @Column(nullable = false, columnDefinition = "bit default 0")
    private boolean isPrivate;
    @Column(nullable = false, columnDefinition = "int default 0")
    private int likes;
    @Column(nullable = false, columnDefinition = "int default 0")
    private int dislikes;
    @Column(nullable = false, columnDefinition = "int default 0")
    private int views;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "translation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TranslationEntry> translations;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creationTime;

    @ManyToMany(mappedBy = "translations")
    @JsonBackReference
    private List<Collection> collections;


    public Translation(String name, String description, String difficulty, String sourceLang, String targetLang, boolean isPrivate, int likes, int dislikes, int views, User user, List<TranslationEntry> translations) {
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
        this.isPrivate = isPrivate;
        this.likes = likes;
        this.dislikes = dislikes;
        this.views = views;
        this.user = user;
        this.translations = translations;
    }

    public Translation() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<TranslationEntry> getTranslations() {
        return translations;
    }

    public void setTranslations(List<TranslationEntry> translations) {
        this.translations = translations;
        for (TranslationEntry entry : translations) {
            entry.setTranslation(this);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Translation that = (Translation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}