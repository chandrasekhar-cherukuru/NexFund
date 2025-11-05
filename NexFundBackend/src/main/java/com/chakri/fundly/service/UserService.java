package com.chakri.fundly.service;

import com.chakri.fundly.model.Users;
import com.chakri.fundly.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserRepo repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Users register(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Users savedUser = repo.save(user);
        System.out.println("✅ User registered: " + savedUser.getUsername() + " | Email: " + savedUser.getEmail());
        return savedUser;
    }

    public String verify(Users user) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(user.getUsername());
        } else {
            return "Authentication failed";
        }
    }

    public Users updateUserProfile(Users user) {
        // Get current logged-in user from JWT token
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users existingUser = repo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getName() != null && !user.getName().isEmpty()) {
            existingUser.setName(user.getName());
        }
        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            existingUser.setUsername(user.getUsername());
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            existingUser.setProfileImage(user.getProfileImage());
        }

        Users updatedUser = repo.save(existingUser);
        System.out.println("✅ Profile updated: " + updatedUser.getUsername() + " | Email: " + updatedUser.getEmail());
        return updatedUser;
    }

    public Users getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = repo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("✅ getCurrentUser: " + user.getUsername() + " | Email: " + user.getEmail());
        return user;
    }
}
