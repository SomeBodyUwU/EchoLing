package com.example.echoling.translation;

import com.example.echoling.annotation.AdvancedTextAnnotator;
import com.example.echoling.auntefication.CookieUtils;
import com.example.echoling.data.Collection.Collection;
import com.example.echoling.data.Collection.CollectionService;
import com.example.echoling.data.Translation.Translation;
import com.example.echoling.data.Translation.TranslationEntry;
import com.example.echoling.data.Translation.TranslationService;
import com.example.echoling.data.User.Administrator.AdministratorService;
import com.example.echoling.data.User.User;
import com.example.echoling.data.User.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.*;

@Controller
public class ReadingController {

    @Autowired
    private CookieUtils cookieUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private TranslationService translationService;
    @Autowired
    private AdvancedTextAnnotator annotator;
    @Autowired
    private AdministratorService administratorService;
    @Autowired
    private CollectionService collectionService;

    @GetMapping("/read")
    public String showReadingPage(HttpServletRequest request, Model model,
                                  @RequestParam("translationId") Long translationId,
                                  @RequestParam(value = "ptt", defaultValue = "1") String pttParam,
                                  @RequestParam(value = "page", defaultValue = "1") int page,
                                  @RequestParam(value = "inverse", defaultValue = "false") boolean inverse,
                                  Principal principal) {
        try {
            Translation translation = translationService.findById(translationId)
                    .orElseThrow(() -> new Exception("Translation not found"));
            boolean isAdmin = false;
            boolean isOwner;
            boolean isOwnerOrAdmin = false;
            if (principal != null && principal.getName() != null) {
                User currentUser = userService.authenticate(principal.getName());
                isAdmin = administratorService.isUserAdmin(currentUser);
                isOwner = translation.getUser().equals(currentUser);
                isOwnerOrAdmin = isOwner || isAdmin;
                List<Collection> userCollections = collectionService.getCollectionsByUser(currentUser);
                model.addAttribute("userCollections", userCollections);
            }

            List<TranslationEntry> translationsMap = translation.getTranslations();
            String sourceLang = translation.getSourceLang();
            String targetLang = translation.getTargetLang();

            if (translationsMap == null || translationsMap.isEmpty()) {
                return "redirect:/translate";
            }

            int totalParagraphs = translationsMap.size();
            int ptt = Integer.parseInt(pttParam);

            int totalGroups = (int) Math.ceil((double) totalParagraphs / ptt);
            int totalPages = totalGroups * 2;

            page = Math.max(1, Math.min(page, totalPages));

            int groupIndex = (page - 1) / 2;

            boolean isOriginalsBlock = inverse == (page % 2 == 0);

            int startIndex = groupIndex * ptt;
            int endIndex = Math.min(startIndex + ptt, totalParagraphs);

            List<String> originals = new ArrayList<>();
            List<String> translationsList = new ArrayList<>();

            for (int i = startIndex; i < endIndex; i++) {
                TranslationEntry paraMap = translationsMap.get(i);
                if (paraMap != null) {
                    originals.add(paraMap.getSourceText());
                    translationsList.add(paraMap.getTranslatedText());
                }
            }

            List<String> selectedParagraphs = isOriginalsBlock ? originals : translationsList;

            StringBuilder processedTextBuilder = new StringBuilder();
            for (String para : selectedParagraphs) {
                processedTextBuilder.append("<p>");

                if (isOriginalsBlock) {
                    processedTextBuilder.append(processFurigana(para, sourceLang));
                } else {
                    processedTextBuilder.append(processFurigana(para, targetLang)).append("\n");
                }

                processedTextBuilder.append("</p>");
            }

            String processedText = processedTextBuilder.toString();

            model.addAttribute("translation", translation);
            model.addAttribute("content", processedText);
            model.addAttribute("ptt", pttParam);
            model.addAttribute("page", page);
            model.addAttribute("inverse", inverse);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalParagraphs", totalParagraphs);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("isOwnerOrAdmin", isOwnerOrAdmin);

            return "read";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/translate";
        }
    }



    private String processFurigana(String text, String language) {
        if(Objects.equals(language, "Japanese"))
            return annotator.annotateJapanese(text);
        else if (Objects.equals(language, "Simplified")||Objects.equals(language, "Traditional"))
            return annotator.annotateChinese(text);
        return text;
    }
}
