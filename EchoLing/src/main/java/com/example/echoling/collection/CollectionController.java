package com.example.echoling.collection;

import com.example.echoling.data.Collection.Collection;
import com.example.echoling.data.Collection.CollectionService;
import com.example.echoling.data.Translation.Translation;
import com.example.echoling.data.Translation.TranslationService;
import com.example.echoling.data.User.User;
import com.example.echoling.data.User.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class CollectionController {

    private final CollectionService collectionService;

    private final UserService userService;
    private final TranslationService translationService;

    public CollectionController(CollectionService collectionService, UserService userService, TranslationService translationService) {
        this.collectionService = collectionService;
        this.userService = userService;
        this.translationService = translationService;
    }

    @GetMapping("/collections")
    public String viewCollections(Model model, Principal principal) {
        User currentUser = userService.authenticate(principal.getName());
        List<Collection> userCollections = collectionService.getCollectionsByUser(currentUser);
        model.addAttribute("userCollections", userCollections);
        return "collections";
    }

    @PostMapping("/collections/create")
    public String createCollection(@RequestParam String name, Principal principal, RedirectAttributes redirectAttributes) {
        User currentUser = userService.authenticate(principal.getName());
        collectionService.createCollection(name, currentUser);
        redirectAttributes.addFlashAttribute("message", "Collection created successfully");
        return "redirect:/collections";
    }

    @PostMapping("/collections/{id}/delete")
    public String deleteCollection(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        User currentUser = userService.authenticate(principal.getName());
        Collection collection = collectionService.findById(id);

        if (!collection.getUser().equals(currentUser)) {
            redirectAttributes.addFlashAttribute("error", "You do not have permission to delete this collection");
            return "redirect:/collections";
        }

        collectionService.deleteCollection(collection);
        redirectAttributes.addFlashAttribute("message", "Collection deleted successfully");
        return "redirect:/collections";
    }
    @PostMapping("/collections/addTranslation")
    public String addTranslationToCollection(@RequestParam Long collectionId, @RequestParam Long translationId, Principal principal, RedirectAttributes redirectAttributes) {
        User currentUser = userService.authenticate(principal.getName());
        Collection collection = collectionService.findById(collectionId);

        if (!collection.getUser().equals(currentUser)) {
            redirectAttributes.addFlashAttribute("error", "You do not have permission to modify this collection");
            return "redirect:/read?translationId=" + translationId;
        }

        Optional<Translation> translation = translationService.findById(translationId);
        translation.ifPresent(value -> collectionService.addTranslationToCollection(collection, value));

        redirectAttributes.addFlashAttribute("message", "Translation added to collection");
        return "redirect:/read?translationId=" + translationId;
    }
    @PostMapping("/collections/removeTranslation")
    public String removeTranslationFromCollection(@RequestParam Long collectionId, @RequestParam Long translationId, Principal principal, RedirectAttributes redirectAttributes) {
        User currentUser = userService.authenticate(principal.getName());
        Collection collection = collectionService.findById(collectionId);

        if (!collection.getUser().equals(currentUser)) {
            redirectAttributes.addFlashAttribute("error", "You do not have permission to modify this collection");
            return "redirect:/collections";
        }

        Optional<Translation> translation = translationService.findById(translationId);
        translation.ifPresent(value -> collectionService.removeTranslationFromCollection(collection, value));

        redirectAttributes.addFlashAttribute("message", "Translation removed from collection");
        return "redirect:/collections";
    }

}