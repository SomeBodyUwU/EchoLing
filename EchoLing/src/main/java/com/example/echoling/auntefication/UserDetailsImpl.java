package com.example.echoling.auntefication;

import com.example.echoling.data.User.Administrator.AdministratorService;
import com.example.echoling.data.User.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.List;

public class UserDetailsImpl implements UserDetails {
    private User user;
    private AdministratorService administratorService;

    public UserDetailsImpl(User user, AdministratorService administratorService) {
        this.user = user;
        this.administratorService = administratorService;
    }

    public User getUser(){
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if (administratorService.isUserAdmin(user))
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}