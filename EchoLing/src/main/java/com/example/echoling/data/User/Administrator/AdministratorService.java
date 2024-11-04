package com.example.echoling.data.User.Administrator;

import com.example.echoling.data.User.User;
import org.springframework.stereotype.Service;

@Service
public class AdministratorService {

    private final AdministratorRepository adminRepo;

    public AdministratorService(AdministratorRepository adminRepo) {
        this.adminRepo = adminRepo;
    }

    public boolean isUserAdmin(User user) {
        return adminRepo.existsByUser(user);
    }
}