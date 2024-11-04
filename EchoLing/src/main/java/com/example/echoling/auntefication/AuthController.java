package com.example.echoling.auntefication;

import com.example.echoling.data.User.Administrator.AdministratorService;
import com.example.echoling.data.User.User;
import com.example.echoling.data.User.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final AdministratorService administratorService;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, AdministratorService administratorService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.administratorService = administratorService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("error", "");
        return "login";
    }

    @GetMapping("/sign-up")
    public String showSignUpForm(Model model) {
        model.addAttribute("error", "");
        return "sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String password2,
            Model model) {

        if (!password.equals(password2)) {
            model.addAttribute("error", "Passwords do not match.");
            return "sign-up";
        }

        try {
            userService.registerUser(username, email, password);
            login(username, password);
            return "redirect:/main";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "sign-up";
        }
    }


    @PostMapping("/login")
    public String loginUser(
            @RequestParam String username,
            @RequestParam String password,
            Model model) {
        try {
            login(username, password);
            return "redirect:/main";
        } catch (Exception e){
            model.addAttribute("error", "Invalid username or password.");
            return "login";
        }
    }

    private void login(String username, String password) throws Exception {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, @RequestParam String password, Principal principal, RedirectAttributes redirectAttributes) {
        User currentUser = userService.authenticate(principal.getName());
        if (!currentUser.isValidPassword(password)) {
            redirectAttributes.addFlashAttribute("error", "Invalid password");
            return "redirect:/read?translationId=" + id;
        }

        if (administratorService.isUserAdmin(currentUser)) {
            User userToDelete = userService.findById(id);
            userService.deleteUser(userToDelete);
            redirectAttributes.addFlashAttribute("message", "User deleted successfully");
            return "redirect:/main";
        } else {
            redirectAttributes.addFlashAttribute("error", "You do not have permission to delete this user");
            return "redirect:/read?translationId=" + id;
        }
    }


//    @GetMapping("/guest")
//    public String guestReg(Model model) {
//        try {
//            return "redirect:/main";
//        } catch (Exception e) {
//            model.addAttribute("error", e.getMessage());
//            return "login";
//        }
//    }
}

