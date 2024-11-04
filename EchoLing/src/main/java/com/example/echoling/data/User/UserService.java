package com.example.echoling.data.User;

import com.example.echoling.data.Translation.Translation;
import com.example.echoling.data.Translation.TranslationService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TranslationService translationService;

    public UserService(UserRepository userRepository, TranslationService translationService) {
        this.userRepository = userRepository;
        this.translationService = translationService;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    @Transactional
    public User registerUser(String username, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with this email already exists.");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);

        return userRepository.save(newUser);
    }

    public User authenticate(String username, String password) throws Exception {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isEmpty()) {
            User user = userOptional.get();
            if (user.isValidPassword(password)) return user;
            else throw new Exception("Password is incorrect");
        }
        userOptional = userRepository.findByEmail(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.isValidPassword(password)) return user;
            else throw new Exception("Password is incorrect");
        }
        throw new Exception("Username or email is incorrect");
    }
    public User authenticate(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) return userOptional.get();
        userOptional = userRepository.findByEmail(username);
        return userOptional.orElse(null);
    }

    public String findUsernameByEmail(String email) throws Exception {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new Exception("Email not found");
        }
        return userOptional.get().getUsername();
    }

    public void changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    public void changeEmail(Long userId, String newEmail) {
        if (userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        user.setEmail(newEmail);
        userRepository.save(user);
    }

    public void updateUser(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("User not found.");
        }
        userRepository.save(user);
    }
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteUserTransactionally(User user) throws Exception {
        for (Translation translation : user.getTranslations())
            translationService.deleteTranslation(translation);

        userRepository.delete(user);
    }
}
