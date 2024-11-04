package com.example.echoling.data.vocabulary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DefinitionRepository extends JpaRepository<Definition, Long> {
    Optional<Definition> findByDefinitionText(String definitionText);
}
