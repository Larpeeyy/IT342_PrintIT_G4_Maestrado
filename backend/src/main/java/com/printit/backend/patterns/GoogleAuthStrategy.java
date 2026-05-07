package com.printit.backend.patterns;

import com.printit.backend.dto.AuthRequest;
import com.printit.backend.entity.User;
import com.printit.backend.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class GoogleAuthStrategy implements AuthStrategy {

    private final UserRepository userRepository;

    public GoogleAuthStrategy(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User authenticate(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Account not found. Please register first."));

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