package com.example.echoling.translation;

import com.example.echoling.data.Collection.Collection;
import com.example.echoling.data.Collection.CollectionService;
import com.example.echoling.data.Translation.Translation;
import com.example.echoling.data.Translation.TranslationEntry;
import com.example.echoling.data.Translation.TranslationService;
import com.example.echoling.data.User.Administrator.AdministratorService;
import com.example.echoling.data.User.User;
import com.example.echoling.data.User.UserService;
import com.example.echoling.statics.DifficultiesList;
import com.example.echoling.statics.Difficulty;
import com.example.echoling.statics.Language;
import com.example.echoling.statics.LanguagesList;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Controller
public class TranslationController {

    private final UserService userService;
    private final LanguagesList languagesList;
    private final DifficultiesList difficultyList;
    private final TranslationService translationService;
    private final GPTService gptService;
    private final AdministratorService administratorService;
    private CollectionService collectionService;

    @Autowired
    public TranslationController(UserService userService, TranslationService translationService, GPTService gptService, AdministratorService administratorService, CollectionService collectionService) {
        this.userService = userService;
        this.administratorService = administratorService;
        this.languagesList = new LanguagesList();
        this.difficultyList = new DifficultiesList();
        this.translationService = translationService;
        this.gptService = gptService;
        this.collectionService = collectionService;
    }


    @GetMapping("/translate")
    public String showTranslationForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        List<Language> langsList = languagesList.getLanguages();
        model.addAttribute("languagesList", langsList);

        List<Difficulty> difficulties = difficultyList.getDifficulties();
        model.addAttribute("difficultyList", difficulties);

        return "translate";
    }

    @PostMapping("/translate")
    public String processTranslation(@RequestParam("sourceLang") String sourceLang,
                                     @RequestParam("targetLang") String targetLang,
                                     @RequestParam("text") String text,
                                     @RequestParam("name") String name,
                                     @RequestParam("description") String description,
                                     @RequestParam("difficulty") String difficulty,
                                     Principal principal,
                                     Model model,
                                     HttpServletRequest request) throws Exception {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        User user = userService.authenticate(username);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            String[] paragraphs = text.split("\n");
            paragraphs = Arrays.stream(paragraphs).filter(u -> !u.trim().isEmpty()).toArray(String[]::new);
            int maxBatchSize = 5000;
            List<String> batches = batchParagraphs(paragraphs, maxBatchSize);

            List<CompletableFuture<String>> futures = new ArrayList<>();
            for (String batch : batches) {
                CompletableFuture<String> futureTranslation = gptService.translateTextAsync(batch, sourceLang, targetLang);
                futures.add(futureTranslation);
            }

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allFutures.join();

            List<String> translatedBatches = futures.stream()
                    .map(CompletableFuture::join)
                    .toList();

            List<TranslationEntry> translations = createTranslations(paragraphs, getTranslatedParagraphs(translatedBatches));

            request.getSession().setAttribute("translations", translations);
            request.getSession().setAttribute("sourceLang", sourceLang);
            request.getSession().setAttribute("targetLang", targetLang);

            Translation translation = new Translation();
            translation.setName(name);
            translation.setDescription(description);
            translation.setDifficulty(difficulty);
            translation.setUser(user);
            translation.setTranslations(translations);
            translation.setSourceLang(sourceLang);
            translation.setTargetLang(targetLang);

            translationService.saveTranslation(translation);

            return "redirect:/read?translationId=" + translation.getId();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "translate";
        }
    }

    private static List<String> getTranslatedParagraphs(List<String> translatedBatches) {
        List<String> translatedParagraphs = new ArrayList<>();
        for (String translatedBatch : translatedBatches) {
            String[] splitted = translatedBatch.split("\n");
            for (String translatedParagraph : splitted) {
                if (!translatedParagraph.trim().isEmpty())
                    translatedParagraphs.add(translatedParagraph.trim());
            }
        }

        return translatedParagraphs;
    }

    private static List<TranslationEntry> createTranslations(String[] paragraphs, List<String> translatedParagraphs) {
        List<TranslationEntry> translations = new ArrayList<>();

        for (int i = 0, j = 0; i < paragraphs.length && j < translatedParagraphs.size(); i++) {
            String paragraph = paragraphs[i].trim();
            if (paragraph.isEmpty()) continue;

            String translatedParagraph = translatedParagraphs.get(j);

            TranslationEntry paragraphTranslation = new TranslationEntry();
            paragraphTranslation.setSourceText(paragraph);
            paragraphTranslation.setTranslatedText(translatedParagraph);
            translations.add(paragraphTranslation);

            j++;
        }
        return translations;
    }

    private List<String> batchParagraphs(String[] paragraphs, int maxBatchSize) {
        List<String> batches = new ArrayList<>();
        StringBuilder currentBatch = new StringBuilder();

        for (String paragraph : paragraphs) {
            paragraph = paragraph.trim();
            if (paragraph.isEmpty()) continue;

            if (currentBatch.length() + paragraph.length() + 1 <= maxBatchSize) {
                if (!currentBatch.isEmpty()) currentBatch.append("\n");
                currentBatch.append(paragraph);
            } else {
                batches.add(currentBatch.toString());
                currentBatch = new StringBuilder(paragraph);
            }
        }

        if (!currentBatch.isEmpty()) batches.add(currentBatch.toString());

        return batches;
    }
    @Transactional
    @PostMapping("/translations/{id}/delete")
    public String deleteTranslation(@PathVariable Long id, @RequestParam String password, Principal principal, RedirectAttributes redirectAttributes) {
        User currentUser = userService.authenticate(principal.getName());
        if (!currentUser.isValidPassword(password)) {
            redirectAttributes.addFlashAttribute("error", "Invalid password");
            return "redirect:/read?translationId=" + id;
        }

        Optional<Translation> translationO = translationService.findById(id);
        boolean isAdmin = administratorService.isUserAdmin(currentUser);
        boolean isOwner = translationO.get().getUser().equals(currentUser);

        if (isOwner || isAdmin) {
            var translation = translationO.get();
            try {
                for (Collection collection : new HashSet<>(translation.getCollections())) {
                    collectionService.removeTranslationFromCollection(collection, translation);
                }

                translationService.deleteTranslation(translation);
            } catch (Exception e) {
                throw e;
            }
            translationService.deleteTranslation(translation);
            redirectAttributes.addFlashAttribute("message", "Translation deleted successfully");
            return "redirect:/main";
        } else {
            redirectAttributes.addFlashAttribute("error", "You do not have permission to delete this translation");
            return "redirect:/read?translationId=" + id;
        }
    }
}
