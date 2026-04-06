package com.printit.backend.patterns;

import com.printit.backend.dto.AuthRequest;
import com.printit.backend.entity.User;
import com.printit.backend.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GoogleAuthStrategy implements AuthStrategy {

    private final UserRepository userRepository;

    public GoogleAuthStrategy(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User authenticate(AuthRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            if (user.getFullName() == null || user.getFullName().isBlank()) {
                user.setFullName(request.getFullName());
                userRepository.save(user);
            }

            return user;
        }

        User newUser = UserFactory.createGoogleUser(
                request.getEmail(),
                request.getFullName()
        );

        return userRepository.save(newUser);
    }
}