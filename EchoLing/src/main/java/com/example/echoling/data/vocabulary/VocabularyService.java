package com.example.echoling.data.vocabulary;

import com.example.echoling.data.User.User;
import com.example.echoling.data.User.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;
    private final WordRepository wordRepository;
    private final DefinitionRepository definitionRepository;
    private final ExampleRepository exampleRepository;
    private final UserRepository userRepository;

    public VocabularyService(VocabularyRepository repository, UserRepository userRepository,
                             WordRepository wordRepository, DefinitionRepository definitionRepository,
                             ExampleRepository exampleRepository) {
        this.vocabularyRepository = repository;
        this.userRepository = userRepository;
        this.wordRepository = wordRepository;
        this.definitionRepository = definitionRepository;
        this.exampleRepository = exampleRepository;
    }

    // Fetch all vocabulary for a specific user
    public List<Vocabulary> getAllVocabularyByUserId(Long userId) {
        return vocabularyRepository.findAllByUserId(userId);
    }

    // Save a new vocabulary entry
    @Transactional
    public boolean saveToDatabase(String wordText, String definitionText, String exampleText, Long userId) {
        // Fetch the user
        Optional<User> userOpt = userRepository.findById(userId);

        User user = userOpt.get();

        // Find or create the Word entity
        Word word = findOrCreateWord(wordText);

        // Find or create the Definition entity
        Definition definition = findOrCreateDefinition(definitionText);

        // Find or create the Example entity
        Example example = findOrCreateExample(exampleText);

        // Now create a new Vocabulary entry
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setUser(user); // Associate with the user
        vocabulary.setWord(word);
        vocabulary.setDefinition(definition);
        vocabulary.setExample(example);
        vocabularyRepository.save(vocabulary);

        return true; // Indicate success
    }

    private Word findOrCreateWord(String wordText) {
        return wordRepository.findByWord(wordText)
                .orElseGet(() -> {
                    Word newWord = new Word();
                    newWord.setWord(wordText);
                    return wordRepository.save(newWord);
                });
    }

    private Definition findOrCreateDefinition(String definitionText) {
        return definitionRepository.findByDefinitionText(definitionText)
                .orElseGet(() -> {
                    Definition newDefinition = new Definition();
                    newDefinition.setDefinitionText(definitionText);
                    return definitionRepository.save(newDefinition);
                });
    }

    private Example findOrCreateExample(String exampleText) {
        return exampleRepository.findByExampleText(exampleText)
                .orElseGet(() -> {
                    Example newExample = new Example();
                    newExample.setExampleText(exampleText);
                    return exampleRepository.save(newExample);
                });
    }

    public boolean deleteVocabularyById(Long id) {
        Optional<Vocabulary> vocabulary = vocabularyRepository.findById(id);
        if (vocabulary.isPresent()) {
            vocabularyRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public boolean isVocabularyBelongingToUser(Long vocabularyId, Long userId) {
        Optional<Vocabulary> vocabularyOpt = vocabularyRepository.findById(vocabularyId);

        // Check if the vocabulary exists and if the user ID matches
        return vocabularyOpt.isPresent() && vocabularyOpt.get().getUser().getId().equals(userId);
    }
}