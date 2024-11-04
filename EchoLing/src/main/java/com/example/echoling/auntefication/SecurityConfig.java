package com.example.echoling.auntefication;

import com.example.echoling.auntefication.JWT.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    private final MyUserDetailsService myUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    private final AAEntryPoint apiAuthenticationEntryPoint;

    public SecurityConfig(MyUserDetailsService myUserDetailsService, JwtRequestFilter jwtRequestFilter, AAEntryPoint apiAuthenticationEntryPoint) {
        this.myUserDetailsService = myUserDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
        this.apiAuthenticationEntryPoint = apiAuthenticationEntryPoint;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return Encryption.hashWithSHA256(rawPassword.toString());
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                String hashedPassword = Encryption.hashWithSHA256(rawPassword.toString());
                return hashedPassword.equals(encodedPassword);
            }
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(MyUserDetailsService userDetailsService){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.authenticationProvider(authenticationProvider(myUserDetailsService));
        return authBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/login", "/sign-up", "/api/**"))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/login", "/sign-up", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/api/authenticate").permitAll()  // Explicitly allow /api/authenticate without authentication
                        .requestMatchers("/api/**").authenticated()  // Require authentication for other /api/** endpoints
                        .requestMatchers("/users/*/delete").hasRole("ADMIN")
                        .requestMatchers("/translations/*/delete", "/collections", "/collections/", "/add-entry", "vocabulary/").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .defaultAuthenticationEntryPointFor(apiAuthenticationEntryPoint, new AntPathRequestMatcher("/api/**"))
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .defaultSuccessUrl("/main", true)
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)  // Ensure filter is in correct position
                .logout(LogoutConfigurer::permitAll)
                .rememberMe(rememberMe -> rememberMe
                        .tokenValiditySeconds(3600 * 24 * 7)
                        .key(System.getenv("LANG_KEY"))
                        .userDetailsService(myUserDetailsService)
                );

        return http.build();
    }

}