package com.example.echoling.data.Collection;

import com.example.echoling.data.Translation.Translation;
import com.example.echoling.data.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findByUser(User user);
}
