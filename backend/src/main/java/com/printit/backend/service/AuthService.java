package com.printit.backend.service;

import com.printit.backend.entity.User;
import com.printit.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User loginWithGoogle(String email, String name) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            if (user.getFullName() == null || user.getFullName().isBlank()) {
                user.setFullName(name);
                userRepository.save(user);
            }

            return user;
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFullName(name);

        // Google users do not need a real password for local login.
        // Leave this null if your DB allows it.
        newUser.setPassword(null);

        // Default role for safety
        newUser.setRole("STUDENT");

        newUser.setStudentId(null);
        newUser.setStaffId(null);

        return userRepository.save(newUser);
    }
}