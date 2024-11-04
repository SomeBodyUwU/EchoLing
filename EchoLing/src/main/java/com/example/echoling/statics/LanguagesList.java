package com.example.echoling.statics;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Scope("prototype")
public class LanguagesList {

    public List<Language> getLanguages() {
        return Arrays.asList(
                new Language("English", "https://www.countryflags.com/wp-content/uploads/united-kingdom-flag-png-large.png"),
                new Language("Ukrainian", "https://www.countryflags.com/wp-content/uploads/ukraine-flag-png-large.png"),
                new Language("Spanish", "https://www.countryflags.com/wp-content/uploads/spain-flag-png-large.png"),
                new Language("German", "https://www.countryflags.com/wp-content/uploads/germany-flag-png-large.png"),
                new Language("French", "https://www.countryflags.com/wp-content/uploads/france-flag-png-large.png"),
                new Language("Japanese", "https://www.countryflags.com/wp-content/uploads/japan-flag-png-large.png"),
                new Language("Korean", "https://www.countryflags.com/wp-content/uploads/south-korea-flag-png-large.png"),
                new Language("Simplified", "https://www.countryflags.com/wp-content/uploads/singapore-flag-png-large.png"),
                new Language("Traditional", "https://www.countryflags.com/wp-content/uploads/taiwan-flag-png-large.png")
        );
    }
}