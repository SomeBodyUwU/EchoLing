package com.example.echoling.recommendation;

import com.example.echoling.auntefication.CookieUtils;
import com.example.echoling.data.Translation.Translation;
import com.example.echoling.data.Translation.TranslationService;
import com.example.echoling.data.User.User;
import com.example.echoling.data.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MainApiController {

    @Autowired
    private CookieUtils cookieUtils;
    private UserService userService;
    private TranslationService translationService;

    @Autowired
    public void setUserRepository(UserService userService, TranslationService translationService) {
        this.userService = userService;
        this.translationService = translationService;
    }

    @GetMapping(value = "/api/main", produces = "application/json")
    public List<Translation> getTranslations(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "difficulty", required = false) String difficulty,
            @RequestParam(value = "sourceLang", required = false) String sourceLang,
            @RequestParam(value = "targetLang", required = false) String targetLang,
            @RequestParam(value = "minViews", required = false) Integer minViews,
            @RequestParam(value = "minDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate minDate,
            @RequestParam(value = "maxDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate maxDate,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "100") int size,
            Principal principal) {
        try {
            List<String> nativeLanguages = new ArrayList<>();
            List<String> learningLanguages = new ArrayList<>();
            if (principal != null) {
                String username = principal.getName();
                User user = userService.authenticate(username);
                if (user != null) {
                    nativeLanguages = user.getNativeLanguages();
                    learningLanguages = user.getLearningLanguages();
                }
            }

            List<Translation> translations = translationService.findTranslationsForUser(
                    nativeLanguages, learningLanguages,
                    search, difficulty, sourceLang, targetLang,
                    minViews, minDate, maxDate, sort, page, size);

            return translations;
        } catch (Exception e) {
            return List.of();
        }
    }
}
