package com.printit.backend.security;

import com.printit.backend.entity.User;
import com.printit.backend.patterns.GoogleUserAdapter;
import com.printit.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;

    public OAuth2LoginSuccessHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        System.out.println("=== GOOGLE OAUTH SUCCESS HANDLER REACHED ===");

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        User adaptedUser = GoogleUserAdapter.convert(oauthUser);

        System.out.println("Google email: " + adaptedUser.getEmail());
        System.out.println("Google name: " + adaptedUser.getFullName());

        User user = authService.loginWithGoogle(
                adaptedUser.getEmail(),
                adaptedUser.getFullName()
        );

        String redirectUrl = "http://localhost:3000/oauth-success"
                + "?email=" + URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8)
                + "&fullName=" + URLEncoder.encode(
                user.getFullName() != null ? user.getFullName() : "",
                StandardCharsets.UTF_8
        )
                + "&role=" + URLEncoder.encode(
                user.getRole() != null ? user.getRole() : "STUDENT",
                StandardCharsets.UTF_8
        );

        response.sendRedirect(redirectUrl);
    }
}