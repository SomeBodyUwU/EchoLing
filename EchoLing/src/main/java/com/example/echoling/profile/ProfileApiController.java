package com.example.echoling.profile;


import com.example.echoling.auntefication.CookieUtils;
import com.example.echoling.data.User.Administrator.AdministratorService;
import com.example.echoling.data.User.User;
import com.example.echoling.statics.LanguagesList;
import com.example.echoling.data.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class ProfileApiController {
    @Autowired
    private CookieUtils cookieUtils;
    private UserService userService;
    private final LanguagesList languagesList;
    @Autowired
    private AdministratorService administratorService;

    public ProfileApiController(LanguagesList languagesList) {
        this.languagesList = languagesList;
    }

    @Autowired
    public void setUserRepository(UserService userService){
        this.userService = userService;
    }



    @GetMapping("/api/profile")
    public ResponseEntity<ProfileResponse> getProfile(Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String username = principal.getName();
        User user = userService.authenticate(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<String> nativeLanguages = user.getNativeLanguages() != null ? user.getNativeLanguages() : List.of();
        List<String> learningLanguages = user.getLearningLanguages() != null ? user.getLearningLanguages() : List.of();
        String role = administratorService.isUserAdmin(user) ? "admin" : "user";

        ProfileResponse profileResponse = new ProfileResponse(
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getSurname(),
                role,
                nativeLanguages,
                learningLanguages
        );

        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    @PutMapping("/api/profile")
    public ResponseEntity<Void> updateProfile(Principal principal,
                                              @RequestBody ProfileUpdateRequest updateRequest) {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String username = principal.getName();
        User user = userService.authenticate(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        user.setName(updateRequest.getName());
        user.setSurname(updateRequest.getSurname());
        user.setNativeLanguages(updateRequest.getNativeLanguages() != null ? updateRequest.getNativeLanguages() : List.of());
        user.setLearningLanguages(updateRequest.getLearningLanguages() != null ? updateRequest.getLearningLanguages() : List.of());

        userService.updateUser(user);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
