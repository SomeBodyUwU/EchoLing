package com.example.echoling.collection;

import com.example.echoling.data.Collection.Collection;
import com.example.echoling.data.Collection.CollectionService;
import com.example.echoling.data.Translation.Translation;
import com.example.echoling.data.Translation.TranslationService;
import com.example.echoling.data.User.User;
import com.example.echoling.data.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/collections")
public class CollectionApiController {

    private final CollectionService collectionService;
    private final UserService userService;
    private final TranslationService translationService;

    @Autowired
    public CollectionApiController(CollectionService collectionService, UserService userService, TranslationService translationService) {
        this.collectionService = collectionService;
        this.userService = userService;
        this.translationService = translationService;
    }

    private CollectionDTO convertToDTO(Collection collection) {
        CollectionDTO dto = new CollectionDTO();
        dto.setName(collection.getName());
        dto.setTranslationIds(collection.getTranslations().stream().map(Translation::getId).collect(Collectors.toList()));
        return dto;
    }

    private void checkOwnership(Collection collection, User user) {
        if (!collection.getUser().equals(user)) {
            throw new AccessDeniedException("You do not have permission to access this collection");
        }
    }

    @GetMapping
    public ResponseEntity<List<CollectionDTO>> getAllCollections(Principal principal) {
        User currentUser = userService.authenticate(principal.getName());
        List<Collection> collections = collectionService.getCollectionsByUser(currentUser);
        List<CollectionDTO> dtos = collections.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CollectionDTO> getCollectionById(@PathVariable Long id, Principal principal) {
        User currentUser = userService.authenticate(principal.getName());
        Collection collection = collectionService.findById(id);

        if (collection==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        checkOwnership(collection, currentUser);

        return ResponseEntity.ok(convertToDTO(collection));
    }

    @PostMapping
    public ResponseEntity<?> createCollection(@RequestBody CollectionDTO collectionDTO, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        User currentUser = userService.authenticate(principal.getName());
        Collection collection = new Collection();
        collection.setName(collectionDTO.getName());
        collection.setUser(currentUser);

        if (collectionDTO.getTranslationIds() != null) {
            List<Translation> translations = translationService.findAllById(collectionDTO.getTranslationIds());
            collection.setTranslations(translations);
        }

        collectionService.saveCollection(collection);

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(collection));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCollection(@PathVariable Long id, @RequestBody CollectionDTO collectionDTO, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        User currentUser = userService.authenticate(principal.getName());
        Collection collection = collectionService.findById(id);

        if (collection==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Collection not found");
        }

        checkOwnership(collection, currentUser);

        collection.setName(collectionDTO.getName());

        if (collectionDTO.getTranslationIds() != null) {
            List<Translation> translations = translationService.findAllById(collectionDTO.getTranslationIds());
            collection.setTranslations(translations);
        }

        collectionService.saveCollection(collection);
        return ResponseEntity.ok(convertToDTO(collection));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCollection(@PathVariable Long id, Principal principal) {
        User currentUser = userService.authenticate(principal.getName());
        Collection collection = collectionService.findById(id);

        if (collection==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Collection not found");
        }

        checkOwnership(collection, currentUser);

        collectionService.deleteCollection(collection);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
