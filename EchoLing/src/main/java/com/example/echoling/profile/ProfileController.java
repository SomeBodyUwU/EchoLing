package com.example.echoling.profile;

import com.example.echoling.auntefication.CookieUtils;
import com.example.echoling.data.User.User;
import com.example.echoling.data.User.UserService;
import com.example.echoling.statics.Language;
import com.example.echoling.statics.LanguagesList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.echoling.data.User.Administrator.AdministratorService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ProfileController {
    @Autowired
    private CookieUtils cookieUtils;
    private UserService userService;
    private final LanguagesList languagesList;
    @Autowired
    private AdministratorService administratorService;

    public ProfileController(LanguagesList languagesList) {
        this.languagesList = languagesList;
    }
    @Autowired
    public void setUserRepository(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String getProfile(Model model, Principal principal) throws Exception {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        User user = userService.authenticate(username);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("name", user.getName());
        model.addAttribute("surname", user.getSurname());

        List<Language> nativeLanguagesList = languagesList.getLanguages();
        List<Language> learningLanguagesList = languagesList.getLanguages();

        List<String> selectedNativeLanguages = user.getNativeLanguages() != null ? user.getNativeLanguages() : new ArrayList<>();
        List<String> selectedLearningLanguages = user.getLearningLanguages() != null ? user.getLearningLanguages() : new ArrayList<>();

        model.addAttribute("role", administratorService.isUserAdmin(user) ? "admin" : "user");
        model.addAttribute("nativeLanguagesList", nativeLanguagesList);
        model.addAttribute("learningLanguagesList", learningLanguagesList);
        model.addAttribute("selectedNativeLanguages", selectedNativeLanguages);
        model.addAttribute("selectedLearningLanguages", selectedLearningLanguages);

        return "profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(
            @RequestParam String name,
            @RequestParam String surname,
            @RequestParam(required = false) List<String> nativeLanguages,
            @RequestParam(required = false) List<String> learningLanguages,
            Principal principal,
            Model model) throws Exception {

        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        User user = userService.authenticate(username);
        if (user == null) {
            return "redirect:/login";
        }

        user.setName(name);
        user.setSurname(surname);
        user.setNativeLanguages(nativeLanguages != null ? nativeLanguages : new ArrayList<>());
        user.setLearningLanguages(learningLanguages != null ? learningLanguages : new ArrayList<>());

        userService.updateUser(user);

        return "redirect:/profile";
    }
}