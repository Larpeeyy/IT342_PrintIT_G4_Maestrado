package com.printit.backend.features.auth;

import com.printit.backend.core.entity.User;

public class UserFactory {

    public static User createGoogleUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setFullName(name);
        user.setPassword(null);
        user.setRole("STUDENT");
        user.setStudentId(null);
        user.setStaffId(null);
        return user;
    }
}