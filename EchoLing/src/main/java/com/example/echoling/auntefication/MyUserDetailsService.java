package com.example.echoling.auntefication;

import com.example.echoling.data.User.Administrator.AdministratorService;
import com.example.echoling.data.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AdministratorService administratorService;

    @Autowired
    public MyUserDetailsService(UserRepository userRepository, AdministratorService administratorService) {
        this.userRepository = userRepository;
        this.administratorService = administratorService;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(usernameOrEmail);
        if(user.isEmpty()){
            user = userRepository.findByEmail(usernameOrEmail);
            if(user.isEmpty()) throw new UsernameNotFoundException("User not found");
        }
        return new UserDetailsImpl(user.get(), administratorService);
    }
}
