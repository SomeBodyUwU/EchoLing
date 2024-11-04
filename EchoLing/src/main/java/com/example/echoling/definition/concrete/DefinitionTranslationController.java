package com.example.echoling.definition.concrete;

import com.example.echoling.auntefication.CookieUtils;
import com.example.echoling.data.User.User;
import com.example.echoling.data.User.UserService;
import com.example.echoling.data.vocabulary.VocabularyService;
import com.example.echoling.definition.Parser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@SessionAttributes("word")
public class DefinitionTranslationController {

    private final DefinitionTranslationService translationService;
    private final UserService userService;
    private final VocabularyService vocabularyService;
    private final CookieUtils cookieUtils;

    public DefinitionTranslationController(DefinitionTranslationService translationService, UserService userService, VocabularyService vocabularyService, CookieUtils cookieUtils) {
        this.translationService = translationService;
        this.userService = userService;
        this.vocabularyService = vocabularyService;
        this.cookieUtils = cookieUtils;
    }

    @GetMapping("/definition")
    public String showMainPage(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Principal principal) {

        if (principal == null)
            return "redirect:/login";
        String username = principal.getName();
        User user = userService.authenticate(username);
        if (user == null)
            return "redirect:/login";

        request.setAttribute("user", user);
        try {
            model.addAttribute("currentLanguage", cookieUtils.getCookie(httpServletRequest, "echoLingCurrentLanguage").get());
        } catch (Exception e) {
            model.addAttribute("currentLanguage", "English");
        }

        return "get_definition";


    }

    @PostMapping("/definition")
    public String ProceedDefinition(@RequestParam String word, @RequestParam String sourceLang, Model model, HttpServletRequest request,
                                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Principal principal) throws Exception {

        model.addAttribute("error", "");

        if (principal == null)
            return "redirect:/login";
        String username = principal.getName();
        User user = userService.authenticate(username);
        if (user == null)
            return "redirect:/login";

        model.addAttribute("word", word);
        try{
        String wordDefinition = Parser.toHtml(Parser.parseChatGPT(translationService.getDefinition(word, sourceLang)), word);
        model.addAttribute("wordDefinition", wordDefinition);
        }
        catch (Exception e){
            model.addAttribute("wordDefinition", "{start}\nChatGPT error.\nPlease try again.\n{end}"); //(java.net.UnknownHostException: No such host is known (api.openai.com))
        }

        cookieUtils.createCookie(httpServletResponse, "echoLingCurrentLanguage", sourceLang, 3600);
        userService.updateUser(user);

        request.setAttribute("user", user);
        try {
            model.addAttribute("currentLanguage", cookieUtils.getCookie(httpServletRequest, "echoLingCurrentLanguage").get());
        } catch (Exception e) {
            model.addAttribute("currentLanguage", "English");
        }
        return "get_definition";

    }

}