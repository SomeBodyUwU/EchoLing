package com.example.echoling.data.Translation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long>, JpaSpecificationExecutor<Translation> {
    List<Translation> findAllById(Iterable<Long> ids);
    List<Translation> findByName(String name);
    @Query(name = "Translation.searchByKeyword")
    List<Translation> searchByKeyword(String keyword);
    @Query("SELECT t FROM Translation t WHERE t.sourceLang IN :sourceLangs " +
            "AND t.targetLang IN :targetLangs AND t.difficulty = :difficulty")
    List<Translation> findByLanguagesAndDifficulty(List<String> sourceLangs, List<String> targetLangs, String difficulty);
}
