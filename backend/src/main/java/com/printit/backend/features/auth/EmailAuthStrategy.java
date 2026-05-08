package com.printit.backend.features.auth;

import com.printit.backend.core.entity.User;
import com.printit.backend.core.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EmailAuthStrategy implements AuthStrategy {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public EmailAuthStrategy(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User authenticate(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Account not found."));

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password.");
        }

        if ("STAFF".equalsIgnoreCase(user.getRole())) {
            String approvalStatus = user.getApprovalStatus();

            if (!"APPROVED".equalsIgnoreCase(approvalStatus)) {
                if ("PENDING".equalsIgnoreCase(approvalStatus)) {
                    throw new RuntimeException("Your staff account is still pending admin approval.");
                }

                if ("REJECTED".equalsIgnoreCase(approvalStatus)) {
                    throw new RuntimeException("Your staff registration was rejected by the admin.");
                }

                throw new RuntimeException("Your staff account is not approved yet.");
            }
        }

        return user;
    }
}