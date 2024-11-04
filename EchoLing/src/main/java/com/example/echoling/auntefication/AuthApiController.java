package com.example.echoling.auntefication;

import com.example.echoling.auntefication.JWT.AuthenticationRequest;
import com.example.echoling.auntefication.JWT.AuthenticationResponse;
import com.example.echoling.auntefication.JWT.JwtUtil;
import com.example.echoling.data.User.Administrator.AdministratorService;
import com.example.echoling.data.User.User;
import com.example.echoling.data.User.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
public class AuthApiController {

    private final AuthenticationManager authenticationManager;

    private final MyUserDetailsService myUserDetailsService;

    private final JwtUtil jwtUtil;

    private final UserService userService;
    private AdministratorService administratorService;

    public AuthApiController(AuthenticationManager authenticationManager, MyUserDetailsService myUserDetailsService, JwtUtil jwtUtil, UserService userService, AdministratorService administratorService) {
        this.authenticationManager = authenticationManager;
        this.myUserDetailsService = myUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.administratorService = administratorService;
    }

    @GetMapping("/api/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/api/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authRequest) throws Exception {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Incorrect username or password");
        }

        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(authRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @DeleteMapping("api/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Principal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(401).body("Authentication required");
        }

        User user = userService.authenticate(principal.getName());
        User userToDelete = userService.findById(id);
        if (userToDelete == null) return ResponseEntity.notFound().build();

        if (user == null) return ResponseEntity.status(403).body("Access denied");
        boolean isAdmin = administratorService.isUserAdmin(user);

        if (!isAdmin)
            return ResponseEntity.status(403).body("Access denied");


        userService.deleteUserTransactionally(userToDelete);

        return ResponseEntity.ok("User deleted successfully");
    }
    @PostMapping("/api/sign-up")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest signUpRequest) {
        try {
            userService.registerUser(signUpRequest.getUsername(), signUpRequest.getPassword(), signUpRequest.getEmail());
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

