package com.printit.backend.patterns;

import com.printit.backend.entity.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class GoogleUserAdapter {

    public static User convert(OAuth2User oauthUser) {
        User user = new User();
        user.setEmail(oauthUser.getAttribute("email"));
        user.setFullName(oauthUser.getAttribute("name"));
        user.setRole("STUDENT");
        return user;
    }
}