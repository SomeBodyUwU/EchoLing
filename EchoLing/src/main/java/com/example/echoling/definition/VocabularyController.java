package com.example.echoling.definition;

import com.example.echoling.data.User.User;
import com.example.echoling.data.User.UserService;
import com.example.echoling.data.vocabulary.VocabularyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@SessionAttributes("word")
public class VocabularyController {

    private final VocabularyService vocabularyService;
    private final UserService userService;

    public VocabularyController(VocabularyService vocabularyService, UserService userService) {
        this.vocabularyService = vocabularyService;
        this.userService = userService;
    }

    @GetMapping("/vocabulary")
    public String vocabulary(Model model, RedirectAttributes redirectAttributes, HttpServletRequest request, Principal principal) {

        if (principal == null)
            return "redirect:/login";
        String username = principal.getName();
        User user = userService.authenticate(username);
        if (user == null)
            return "redirect:/login";

        request.setAttribute("user", user);
        model.addAttribute("vocabularyList", vocabularyService.getAllVocabularyByUserId(user.getId()));
        return "vocabulary";
    }

    @DeleteMapping("/vocabulary/{id}")
    public ResponseEntity<String> deleteVocabulary(@PathVariable Long id, Principal principal) {

        if (principal == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add entry.");
        String username = principal.getName();
        User user = userService.authenticate(username);
        if (user == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add entry.");
        boolean isDeleted = false;
        if (vocabularyService.isVocabularyBelongingToUser(id, user.getId()))
            isDeleted = vocabularyService.deleteVocabularyById(id);


        if (isDeleted) {
            return ResponseEntity.ok().build(); // Return 200 OK if the deletion was successful
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if the entry wasn't found
        }
    }
}
