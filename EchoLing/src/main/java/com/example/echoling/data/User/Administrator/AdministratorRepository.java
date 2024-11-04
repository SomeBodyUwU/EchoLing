package com.example.echoling.data.User.Administrator;

import com.example.echoling.data.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    Administrator findByUser(User user);
    boolean existsByUser(User user);
}