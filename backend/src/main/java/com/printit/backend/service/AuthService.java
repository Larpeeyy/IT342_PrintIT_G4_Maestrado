package com.printit.backend.service;

import com.printit.backend.dto.AuthRequest;
import com.printit.backend.entity.User;
import com.printit.backend.patterns.EmailAuthStrategy;
import com.printit.backend.patterns.GoogleAuthStrategy;
import com.printit.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailAuthStrategy emailAuthStrategy;
    private final GoogleAuthStrategy googleAuthStrategy;
    private final NotificationService notificationService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailAuthStrategy emailAuthStrategy,
            GoogleAuthStrategy googleAuthStrategy,
            NotificationService notificationService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailAuthStrategy = emailAuthStrategy;
        this.googleAuthStrategy = googleAuthStrategy;
        this.notificationService = notificationService;
    }

    public User register(AuthRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            throw new RuntimeException("Email is already registered.");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        if ("STUDENT".equalsIgnoreCase(request.getRole())) {
            user.setStudentId(request.getStudentId());
            user.setStaffId(null);
            user.setApprovalStatus("APPROVED");
        } else if ("STAFF".equalsIgnoreCase(request.getRole())) {
            user.setStaffId(request.getStaffId());
            user.setStudentId(null);
            user.setApprovalStatus("PENDING");
        } else if ("ADMIN".equalsIgnoreCase(request.getRole())) {
            user.setStudentId(null);
            user.setStaffId(null);
            user.setApprovalStatus("APPROVED");
        } else {
            user.setStudentId(null);
            user.setStaffId(null);
            user.setApprovalStatus("APPROVED");
        }

        user.setUsername(request.getEmail().split("@")[0]);
        user.setProfileImageUrl(null);

        User savedUser = userRepository.save(user);

        if ("STAFF".equalsIgnoreCase(request.getRole())) {
            List<User> admins = userRepository.findByRole("ADMIN");
            for (User admin : admins) {
                notificationService.createNotification(
                        admin,
                        "New Staff Approval Request",
                        request.getFullName() + " registered as staff and is waiting for approval.",
                        "STAFF_APPROVAL"
                );
            }
        }

        return savedUser;
    }

    public User login(String email, String password) {
        AuthRequest request = new AuthRequest();
        request.setEmail(email);
        request.setPassword(password);

        return emailAuthStrategy.authenticate(request);
    }

    public User loginWithGoogle(String email, String name) {
        AuthRequest request = new AuthRequest();
        request.setEmail(email);
        request.setFullName(name);

        return googleAuthStrategy.authenticate(request);
    }
}