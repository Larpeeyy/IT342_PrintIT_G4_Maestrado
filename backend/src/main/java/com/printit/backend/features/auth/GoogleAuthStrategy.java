package com.printit.backend.features.auth;

import com.printit.backend.core.entity.User;
import com.printit.backend.core.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class GoogleAuthStrategy implements AuthStrategy {

    private final UserRepository userRepository;

    public GoogleAuthStrategy(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User authenticate(AuthRequest request) {
        if (request == null || request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Google email is required.");
        }

        String email = request.getEmail().trim().toLowerCase();
        String fullName = request.getFullName() != null && !request.getFullName().trim().isEmpty()
                ? request.getFullName().trim()
                : generateNameFromEmail(email);

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            if ("STAFF".equalsIgnoreCase(user.getRole())) {
                throw new RuntimeException("Staff accounts must login manually using email and password.");
            }

            return user;
        }

        User newGoogleStudent = new User();
        newGoogleStudent.setEmail(email);
        newGoogleStudent.setFullName(fullName);
        newGoogleStudent.setUsername(generateUniqueUsername(email));
        newGoogleStudent.setPassword(generateGoogleOnlyPassword());
        newGoogleStudent.setRole("STUDENT");
        newGoogleStudent.setStudentId("");
        newGoogleStudent.setStaffId("");
        newGoogleStudent.setApprovalStatus("APPROVED");
        newGoogleStudent.setProfileImageUrl("");

        return userRepository.save(newGoogleStudent);
    }

    private String generateNameFromEmail(String email) {
        String namePart = email.split("@")[0]
                .replace(".", " ")
                .replace("_", " ")
                .replace("-", " ")
                .trim();

        if (namePart.isEmpty()) {
            return "Google User";
        }

        String[] words = namePart.split("\\s+");
        StringBuilder formattedName = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                formattedName
                        .append(Character.toUpperCase(word.charAt(0)))
                        .append(word.length() > 1 ? word.substring(1).toLowerCase() : "")
                        .append(" ");
            }
        }

        return formattedName.toString().trim();
    }

    private String generateUniqueUsername(String email) {
        String baseUsername = email.split("@")[0]
                .replaceAll("[^a-zA-Z0-9._-]", "")
                .toLowerCase();

        if (baseUsername.isBlank()) {
            baseUsername = "googleuser";
        }

        String username = baseUsername;
        int counter = 1;

        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }

    private String generateGoogleOnlyPassword() {
        return "GOOGLE_AUTH_" + UUID.randomUUID();
    }
}