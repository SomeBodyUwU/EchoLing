package com.example.echoling.recommendation;

import com.example.echoling.auntefication.CookieUtils;
import com.example.echoling.data.Translation.TranslationService;
import com.example.echoling.data.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class MainController {
    @Autowired
    private CookieUtils cookieUtils;
    private UserService userService;
    private TranslationService translationService;

    @Autowired
    public void setUserRepository(UserService userService, TranslationService translationService) {
        this.userService = userService;
        this.translationService = translationService;
    }

    @GetMapping("/")
    public String showMainRedirectForm(Model model){
        return "redirect:/main";
    }

    @GetMapping("/main")
    public String showMainForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        return "main";
    }
}
