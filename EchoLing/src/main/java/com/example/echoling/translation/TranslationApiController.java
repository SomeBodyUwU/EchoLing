package com.example.echoling.translation;

import com.example.echoling.data.Collection.Collection;
import com.example.echoling.data.Collection.CollectionService;
import com.example.echoling.data.Translation.Translation;
import com.example.echoling.data.Translation.TranslationEntry;
import com.example.echoling.data.Translation.TranslationService;
import com.example.echoling.data.User.Administrator.AdministratorService;
import com.example.echoling.data.User.User;
import com.example.echoling.data.User.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/translations")
public class TranslationApiController {
    private final TranslationService translationService;
    private final UserService userService;
    private final AdministratorService administratorService;
    private final TransactionTemplate transactionalTemplate;
    private CollectionService collectionService;


    public TranslationApiController(TranslationService translationService, UserService userService, AdministratorService administratorService, TransactionTemplate transactionalTemplate, CollectionService collectionService) {
        this.translationService = translationService;
        this.userService = userService;
        this.administratorService = administratorService;
        this.transactionalTemplate = transactionalTemplate;
        this.collectionService = collectionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTranslation(@PathVariable Long id, Principal principal) {
        Optional<Translation> translationOpt = translationService.findById(id);
        if (translationOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Translation translation = translationOpt.get();

        if (translation.isPrivate()) {
            if (principal == null) {
                return ResponseEntity.status(403).body("Access denied");
            }

            User currentUser = userService.authenticate(principal.getName());
            boolean isAdmin = administratorService.isUserAdmin(currentUser);
            boolean isOwner = translation.getUser().equals(currentUser);

            if (!isAdmin && !isOwner) {
                return ResponseEntity.status(403).body("Access denied");
            }
        }

        return ResponseEntity.ok(translation);
    }

    @PostMapping
    public ResponseEntity<?> createTranslation(@RequestBody TranslationRequest translationRequest, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
        }

        User currentUser = userService.authenticate(principal.getName());

        Translation translation = new Translation();
        translation.setName(translationRequest.getName());
        translation.setDescription(translationRequest.getDescription());
        translation.setDifficulty(translationRequest.getDifficulty());
        translation.setSourceLang(translationRequest.getSourceLang());
        translation.setTargetLang(translationRequest.getTargetLang());
        translation.setPrivate(translationRequest.getIsPrivate());
        translation.setLikes(0);
        translation.setDislikes(0);
        translation.setViews(0);
        translation.setUser(currentUser);

        translationService.saveTranslation(translation);

        return ResponseEntity.status(HttpStatus.CREATED).body(translation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTranslation(@PathVariable Long id, @RequestBody TranslationUpdateRequest updateRequest, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
        }

        Optional<Translation> translationOpt = translationService.findById(id);
        if (translationOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Translation not found");
        }

        Translation existingTranslation = translationOpt.get();
        User currentUser = userService.authenticate(principal.getName());
        boolean isAdmin = administratorService.isUserAdmin(currentUser);
        boolean isOwner = existingTranslation.getUser().equals(currentUser);

        if (!isAdmin && !isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        if (updateRequest.getName() != null) {
            existingTranslation.setName(updateRequest.getName());
        }
        if (updateRequest.getDescription() != null) {
            existingTranslation.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getDifficulty() != null) {
            existingTranslation.setDifficulty(updateRequest.getDifficulty());
        }
        if (updateRequest.getSourceLang() != null) {
            existingTranslation.setSourceLang(updateRequest.getSourceLang());
        }
        if (updateRequest.getTargetLang() != null) {
            existingTranslation.setTargetLang(updateRequest.getTargetLang());
        }
        if (updateRequest.getIsPrivate() != null) {
            existingTranslation.setPrivate(updateRequest.getIsPrivate());
        }

        translationService.saveTranslation(existingTranslation);

        return ResponseEntity.ok(existingTranslation);
    }

    @PutMapping("/{id}/entries")
    public ResponseEntity<?> updateTranslationEntries(@PathVariable Long id, @RequestBody List<TranslationEntryRequest> entries, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
        }

        Optional<Translation> translationOpt = translationService.findById(id);
        if (translationOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Translation not found");
        }

        Translation translation = translationOpt.get();
        User currentUser = userService.authenticate(principal.getName());
        boolean isAdmin = administratorService.isUserAdmin(currentUser);
        boolean isOwner = translation.getUser().equals(currentUser);

        if (!isAdmin && !isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        translation.getTranslations().clear();

        List<TranslationEntry> updatedEntries = new ArrayList<>();
        for (TranslationEntryRequest entryRequest : entries) {
            TranslationEntry entry = new TranslationEntry();
            entry.setSourceText(entryRequest.getSourceText());
            entry.setTranslatedText(entryRequest.getTranslatedText());
            entry.setTranslation(translation);
            updatedEntries.add(entry);
        }

        translation.setTranslations(updatedEntries);
        translationService.saveTranslation(translation);

        return ResponseEntity.ok(translation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTranslation(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Authentication required");
        }

        Optional<Translation> translationOpt = translationService.findById(id);

        if (translationOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Translation translation = translationOpt.get();
        User currentUser = userService.authenticate(principal.getName());
        boolean isAdmin = administratorService.isUserAdmin(currentUser);
        boolean isOwner = translation.getUser().equals(currentUser);

        if (!isAdmin && !isOwner) {
            return ResponseEntity.status(403).body("Access denied");
        }
        try {
            for (Collection collection : new HashSet<>(translation.getCollections())) {
                collectionService.removeTranslationFromCollection(collection, translation);
            }

            translationService.deleteTranslation(translation);
        } catch (Exception e) {
            throw e;
        }

        return ResponseEntity.ok("Translation deleted successfully");
    }


}
