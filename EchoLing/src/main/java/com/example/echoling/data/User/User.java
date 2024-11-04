package com.example.echoling.data.User;

import com.example.echoling.auntefication.Encryption;
import com.example.echoling.data.Translation.Translation;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    private String name;
    private String surname;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creationTime;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Translation> translations;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_native_languages", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "language")
    private List<String> nativeLanguages;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_learning_languages", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "language")
    private List<String> learningLanguages;

    public User() {}

    public User(Long id, String username, String email, String password, String name, String surname,
                List<String> nativeLanguages, List<String> learningLanguages) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = Encryption.hashWithSHA256(password);
        this.name = name;
        this.surname = surname;
        this.nativeLanguages = nativeLanguages;
        this.learningLanguages = learningLanguages;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isValidPassword(String password) {
        return Objects.equals(this.password, Encryption.hashWithSHA256(password));
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

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = Encryption.hashWithSHA256(password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(username, user.username) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }

    public List<Translation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<Translation> translations) {
        this.translations = translations;
    }
}
