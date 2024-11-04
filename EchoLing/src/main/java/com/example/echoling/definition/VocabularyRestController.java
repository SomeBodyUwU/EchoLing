package com.example.echoling.definition;

import com.example.echoling.data.User.User;
import com.example.echoling.data.User.UserService;
import com.example.echoling.data.vocabulary.VocabularyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@SessionAttributes("word")
public class VocabularyRestController {


    private final UserService userService;
    private final VocabularyService vocabularyService;

    public VocabularyRestController(UserService userService, VocabularyService vocabularyService) {
        this.userService = userService;
        this.vocabularyService = vocabularyService;
    }

    @PostMapping("/add-entry")
    public ResponseEntity<String> addEntry(@RequestParam String definition,
                                           @RequestParam String example,
                                           @RequestParam String word, HttpServletRequest request, Model model, Principal principal) {

        if (principal == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add entry.");
        String username = principal.getName();
        User user = userService.authenticate(username);
        if (user == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add entry.");

        boolean success = vocabularyService.saveToDatabase(word, definition, example, user.getId());

        if (success) {
            return ResponseEntity.ok("Entry added successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add entry.");
        }
    }
}
