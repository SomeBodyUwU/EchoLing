package com.example.echoling.data.User.Administrator;

import com.example.echoling.data.User.User;
import jakarta.persistence.*;

@Entity
@Table(name = "administrators")
public class Administrator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public Administrator() {}

    public Administrator(User user) {
        this.user = user;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}