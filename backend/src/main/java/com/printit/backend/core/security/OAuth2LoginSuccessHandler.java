package com.printit.backend.core.security;

import com.printit.backend.core.entity.User;
import com.printit.backend.features.auth.AuthService;
import com.printit.backend.features.auth.GoogleUserAdapter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final String WEB_REDIRECT_URL = "http://localhost:3000/oauth-success";
    private static final String MOBILE_REDIRECT_URL = "printit://oauth-success";
    private static final String OAUTH_SOURCE_SESSION_KEY = "PRINTIT_OAUTH_SOURCE";

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

        String source = getOAuthSource(request);
        String baseRedirectUrl = "mobile".equalsIgnoreCase(source)
                ? MOBILE_REDIRECT_URL
                : WEB_REDIRECT_URL;

        clearOAuthSource(request);

        String redirectUrl = baseRedirectUrl
                + "?id=" + encode(user.getId() != null ? user.getId().toString() : "")
                + "&email=" + encode(user.getEmail())
                + "&fullName=" + encode(user.getFullName())
                + "&username=" + encode(user.getUsername())
                + "&role=" + encode(user.getRole() != null ? user.getRole() : "STUDENT")
                + "&studentId=" + encode(user.getStudentId())
                + "&staffId=" + encode(user.getStaffId())
                + "&profileImageUrl=" + encode(user.getProfileImageUrl())
                + "&approvalStatus=" + encode(user.getApprovalStatus());

        System.out.println("OAuth redirect source: " + source);
        System.out.println("OAuth redirect URL: " + redirectUrl);

        response.sendRedirect(redirectUrl);
    }

    private String getOAuthSource(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return "web";
        }

        Object source = session.getAttribute(OAUTH_SOURCE_SESSION_KEY);

        if (source == null) {
            return "web";
        }

        return source.toString();
    }

    private void clearOAuthSource(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.removeAttribute(OAUTH_SOURCE_SESSION_KEY);
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value != null ? value : "", StandardCharsets.UTF_8);
    }
}