package com.example.echoling.data.vocabulary;

import com.example.echoling.data.User.User;
import jakarta.persistence.*;

@Entity
@Table(name = "vocabulary")
public class Vocabulary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationships
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "definition_id", nullable = true)
    private Definition definition;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "example_id", nullable = true)
    private Example example;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public Definition getDefinition() {
        return definition;
    }

    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    public Example getExample() {
        return example;
    }

    public void setExample(Example example) {
        this.example = example;
    }
}



